package io.prismic;

import java.util.*;

import com.fasterxml.jackson.databind.*;

public class Response {

  private final List<Document> results;
  private final int page;
  private final int results_per_page;
  private final int total_results_size;
  private final int total_pages;
  private final String next_page;
  private final String prev_page;

  public Response(List<Document> results, int page, int results_per_page, int total_results_size, int total_pages, String next_page, String prev_page){
    this.results = results;
    this.page = page;
    this.results_per_page = results_per_page;
    this.total_results_size = total_results_size;
    this.total_pages = total_pages;
    this.next_page = next_page;
    this.prev_page = prev_page;
  }

  public List<Document> getResults() {
    return this.results;
  }
  public int getPage() {
    return this.page;
  }
  public int getResultsPerPage() {
    return this.results_per_page;
  }
  public int getTotalResultsSize() {
    return this.total_results_size;
  }
  public int getTotalPages() {
    return this.total_pages;
  }
  public String getNextPage() {
    return this.next_page;
  }
  public String getPrevPage() {
    return this.prev_page;
  }

  static Response parse(JsonNode json) {
    Iterator<JsonNode> resultsJson = null;
    resultsJson = json.path("results").elements();
    List<Document> results = new ArrayList<Document>();
    while (resultsJson.hasNext()) {
      results.add(Document.parse(resultsJson.next()));
    }
    return new Response(results,
      Integer.parseInt(json.path("page").asText()),
      Integer.parseInt(json.path("results_per_page").asText()),
      Integer.parseInt(json.path("total_results_size").asText()),
      Integer.parseInt(json.path("total_pages").asText()),
      json.path("next_page").asText().equals("null") ? null : json.path("next_page").asText(),
      json.path("prev_page").asText().equals("null") ? null : json.path("prev_page").asText()
    );
  }

}
