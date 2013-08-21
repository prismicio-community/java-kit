package io.prismic;

import java.util.*;

public class Main {

  public static void main(String[] args) {
    System.out.println("GO!");

    Api api = Api.get("http://lesbonneschoses.wroom.io/api");
    Form.Search products = api.getForm("products");

    products.ref("UgjWQN_mqa8HvPJY").query("[:d = at(document.tags, [\"Macaron\"])]");
    System.out.println(products);

    List<Document> documents = products.submit();

    for(Document doc: documents) {
      System.out.println(doc + " -> " + doc.get("product.name"));

      /*for(Map.Entry<String,Fragment> e: doc.getFragments().entrySet()) {
        System.out.println(e.getKey() + " -> " + e.getValue());
      }*/
    }

    System.out.println("DONE.");
  }

}