package com.jspiders.product.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.jspiders.product.Repository.ProductRepo;
import com.jspiders.product.pojo.Product;

@RestController
public class ProductController {

	@Autowired
	private ProductRepo productRepo;

	private final String apiUrl = "https://s3.amazonaws.com/roxiler.com/product_transaction.json";

	@GetMapping(path = "/init")
	public String initializeDatabase() {
		RestTemplate restTemplate = new RestTemplate();
		Product[] products = restTemplate.getForObject(apiUrl, Product[].class);
		List<Product> list = new ArrayList<>();
		for (Product product : products) {
			list.add(product);
		}
		productRepo.saveAll(list);
		return "Data is inserted Successfully.... ";
	}

	@GetMapping(path = "/products/{search}")
	public List<Product> findProducts(@PathVariable String search) {
		try {
			double price = Double.parseDouble(search);
			return productRepo.findProductsByPrice(price);
		} catch (NumberFormatException e) {
			return productRepo.findByTitleContainingOrDescriptionContaining(search);
		}
	}

	@GetMapping(path = "/statistics")
	public double[] getStatistics(@RequestParam(name = "month", required = false, defaultValue = "01") String month) {
		double totalSaleAmount = 0;
		int sold = 0;
		int unSold = 0;

		List<Product> products = productRepo.findAll();
		for (Product product : products) {
			if (product.getDateOfSale().contains("-" + month + "-")) {
				if (product.isSold()) {
					sold++;
					totalSaleAmount += product.getPrice();
				} else {
					unSold++;
				}
			}
		}
		double[] statistics = new double[3];
		statistics[0] = totalSaleAmount;
		statistics[1] = sold;
		statistics[2] = unSold;
		return statistics;
	}

	@GetMapping(path = "/barchart")
	public ResponseEntity<Map<String, Integer>> getBarChart(
			@RequestParam(name = "month", required = false, defaultValue = "01") String month) {

		Map<String, Integer> priceRanges = new HashMap<>();
		priceRanges.put("0-100", 0);
		priceRanges.put("101-200", 0);
		priceRanges.put("201-300", 0);
		priceRanges.put("301-400", 0);
		priceRanges.put("401-500", 0);
		priceRanges.put("501-600", 0);
		priceRanges.put("601-700", 0);
		priceRanges.put("701-800", 0);
		priceRanges.put("801-900", 0);
		priceRanges.put("901-above", 0);

		List<Product> products = productRepo.findAll();
		int[] array = new int[10];
		for (Product product : products) {
			if (product.getDateOfSale().contains("-" + month + "-")) {
				double price = product.getPrice();
				if (price >= 0 && price <= 100) {
					priceRanges.put("0-100", priceRanges.get("0-100") + 1);
				} else if (price >= 101 && price <= 200) {
					priceRanges.put("101-200", priceRanges.get("101-200") + 1);
				} else if (price >= 201 && price <= 300) {
					priceRanges.put("201-300", priceRanges.get("201-300") + 1);
				} else if (price >= 301 && price <= 400) {
					priceRanges.put("301-400", priceRanges.get("301-400") + 1);
				} else if (price >= 401 && price <= 500) {
					priceRanges.put("401-500", priceRanges.get("401-500") + 1);
				} else if (price >= 501 && price <= 600) {
					priceRanges.put("501-600", priceRanges.get("501-600") + 1);
				} else if (price >= 601 && price <= 700) {
					priceRanges.put("601-700", priceRanges.get("601-700") + 1);
				} else if (price >= 701 && price <= 800) {
					priceRanges.put("701-800", priceRanges.get("701-800") + 1);
				} else if (price >= 801 && price <= 900) {
					priceRanges.put("801-900", priceRanges.get("801-900") + 1);
				} else if (price >= 901) {
					priceRanges.put("901-above", priceRanges.get("901-above") + 1);
				}
			}
		}
		return ResponseEntity.ok(priceRanges);
	}

	@GetMapping("/piechart")
	public ResponseEntity<Map<String, Integer>> getPieChart(@RequestParam(name = "month") String month) {
		List<Product> products = productRepo.findAll();
		Map<String, Integer> categoryCounts = new HashMap<>();

		for (Product product : products) {
			if (product.getDateOfSale().contains("-" + month + "-")) {
				String category = product.getCategory();
				categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
			}
		}

		return ResponseEntity.ok(categoryCounts);
	}

	@GetMapping("/combined")
	public ResponseEntity<Map<String, Object>> getCombinedData(
			@RequestParam(name = "month", required = false, defaultValue = "01") String month) {
		double[] statistics = getStatistics(month);
		Map<String, Integer> barChart = getBarChart(month).getBody();
		Map<String, Integer> pieChart = getPieChart(month).getBody();

		Map<String, Object> combinedData = new HashMap<>();
		combinedData.put("statistics", statistics);
		combinedData.put("barChart", barChart);
		combinedData.put("pieChart", pieChart);

		return ResponseEntity.ok(combinedData);
	}
}