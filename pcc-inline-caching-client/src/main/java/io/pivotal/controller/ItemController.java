package io.pivotal.controller;

import org.apache.geode.cache.Region;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemController {

	@Autowired
	Region<String, PdxInstance> itemRegion;
	
	@RequestMapping(method = RequestMethod.GET, path = "/show")
	@ResponseBody
	public String show() throws Exception {
		StringBuilder result = new StringBuilder();
		
		itemRegion.values().forEach(item->result.append(JSONFormatter.toJSON(item)+"<br/>"));

		return result.toString();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/clear")
	@ResponseBody
	public String clearCache() throws Exception {
		itemRegion.removeAll(itemRegion.keySetOnServer());
		return "Region cleared";
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String search(@RequestParam(value = "reference", required = true) String id) {

		PdxInstance result = itemRegion.get(id);

		return JSONFormatter.toJSON(result);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/payment")
	public @ResponseBody String createPayment(@RequestBody String payment) {

		PdxInstance pdxInstance = JSONFormatter.fromJSON(payment);
		
		itemRegion.put(pdxInstance.getField("reference").toString(), pdxInstance);
		
		return "POST Done.";
	}

}
