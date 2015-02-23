package io.prismic;

import java.util.*;

public class Main {

  public static void main(String[] args) {
    System.out.println("GO!");

    Api api = Api.get("https://lesbonneschoses.prismic.io/api");
    Ref ref = api.getMaster();
    Form.SearchForm products = api.getForm("products");

    products.ref(ref).query("[[:d = at(document.tags, [\"Macaron\"])]]");
    System.out.println(products);

    List<Document> documents = products.submit().getResults();

    for(Document doc: documents) {
      System.out.println(doc + " -> ");
      System.out.println(doc.asHtml(new SimpleLinkResolver() {
        public String resolve(Fragment.DocumentLink link) {
          return "kiki";
        }
      }));

      /*for(Map.Entry<String,Fragment> e: doc.getFragments().entrySet()) {
        System.out.println(e.getKey() + " -> " + e.getValue());
      }*/
    }

    System.out.println("DONE.");
  }
  
}
