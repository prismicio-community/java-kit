package io.prismic;

import java.util.*;

import com.fasterxml.jackson.databind.*;

public class Documents {

	List<Document> results;

	public Documents(List<Document> results){
		this.results = results;
	}

	public List<Document> getResults() {
		return this.results;
	}

	static Documents parse(JsonNode json, FragmentParser fragmentParser) {
		Iterator<JsonNode> resultsJson = null;
		resultsJson = json.path("results").elements();
		List<Document> results = new ArrayList<Document>();
		while (resultsJson.hasNext()) {
			results.add(Document.parse(resultsJson.next(), fragmentParser));
		}
		return new Documents(results);
	}
}
