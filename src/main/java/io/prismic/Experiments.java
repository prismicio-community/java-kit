package io.prismic;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * All experiments exposed by the Prismic API
 */
public class Experiments {

  private final List<Experiment> draft;
  private final List<Experiment> running;
  private List<Experiment> all = null;

  public Experiments(List<Experiment> draft, List<Experiment> running) {
    this.draft = Collections.unmodifiableList(draft);
    this.running = Collections.unmodifiableList(running);
  }

  /**
   * All experiments, draft and running
   */
  public synchronized List<Experiment> getAll() {
    if (all == null) {
      all = new ArrayList<Experiment>();
      all.addAll(this.draft);
      all.addAll(this.running);
    }
    return all;
  }

  public List<Experiment> getDraft() {
    return draft;
  }

  public List<Experiment> getRunning() {
    return running;
  }

  /**
   * First running experiment. To be used as the current running experiment
   * null if no running experiment.
   */
  public Experiment getCurrent() {
    if (this.running.size() > 0) {
      return this.running.get(0);
    }
    return null;
  }

  /**
   * Get the current running experiment variation ref from a cookie content
   */
  String refFromCookie(String cookie) {
    if (cookie == null || "".equals(cookie)) {
      return null;
    }
    String[] splitted = cookie.trim().split("%20");
    if (splitted.length >= 2) {
      Experiment exp = findRunningById(splitted[0]);
      if (exp == null) {
        return null;
      }
      int varIndexStr = Integer.parseInt(splitted[1]);
      List<Variation> variations = exp.getVariations();
      if (varIndexStr > -1 && varIndexStr < variations.size()) {
        return variations.get(varIndexStr).getRef();
      }
    }
    return null;
  }

  static Experiments parse(JsonNode json) {
    List<Experiment> draft = new ArrayList<Experiment>();
    Iterator<JsonNode> draftJson = json.withArray("running").elements();
    while(draftJson.hasNext()) {
      draft.add(Experiment.parse(draftJson.next()));
    }

    List<Experiment> running = new ArrayList<Experiment>();
    Iterator<JsonNode> runningJson = json.withArray("running").elements();
    while(runningJson.hasNext()) {
      running.add(Experiment.parse(runningJson.next()));
    }

    return new Experiments(draft, running);
  }

  private Experiment findRunningById(String expId) {
    if (expId == null) return null;
    for (Experiment exp: this.running) {
      if (expId.equals(exp.getGoogleId())) {
        return exp;
      }
    }
    return null;
  }

}
