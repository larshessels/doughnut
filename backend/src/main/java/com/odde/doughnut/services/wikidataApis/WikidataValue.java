package com.odde.doughnut.services.wikidataApis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odde.doughnut.entities.Coordinate;
import com.odde.doughnut.services.wikidataApis.thirdPartyEntities.WikidataClaimItem;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class WikidataValue {
  private WikidataClaimItem wikidataClaimItem;
  private String type;
  private String wikiClass;

  public WikidataValue(WikidataClaimItem wikidataClaimItem, JsonNode value, String type) {
    this.wikidataClaimItem = wikidataClaimItem;
    this.type = type;

    if (isWikibase()) {
      wikiClass = value.get("id").textValue();
      return;
    }
  }

  private boolean isWikibase() {
    return "wikibase-entityid".compareToIgnoreCase(type) == 0;
  }

  private boolean isTimeValue() {
    return "time".compareToIgnoreCase(type) == 0;
  }

  private boolean isStringValue() {
    return "string".compareToIgnoreCase(type) == 0;
  }

  private boolean isGlobeCoordinate() {
    return "globecoordinate".compareToIgnoreCase(type) == 0;
  }

  public WikidataId toWikiClass() {
    return new WikidataId(wikiClass);
  }

  public Coordinate getCoordinate() {
    if (isGlobeCoordinate()) {
      Map<String, Object> data =
          new ObjectMapper()
              .convertValue(this.wikidataClaimItem.getValue1(), new TypeReference<>() {});

      var latitude = (Double) data.get("latitude");
      var longitude = (Double) data.get("longitude");
      return new Coordinate(latitude, longitude);
    }
    return new Coordinate(getStringValue());
  }

  private String getStringValue() {
    if (!isStringValue()) {
      throw new RuntimeException("Unsupported wikidata value type: " + type + ", expected string");
    }
    return this.wikidataClaimItem.getValue1().textValue();
  }

  public String format() {
    if (!isTimeValue()) {
      throw new RuntimeException("Unsupported wikidata value type: " + type + ", expected time");
    }
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("dd MMMM yyyy")
            .withZone(ZoneId.systemDefault())
            .localizedBy(Locale.ENGLISH);
    String inputTime = this.wikidataClaimItem.getValue1().get("time").textValue();
    Instant instant = Instant.parse(inputTime.substring(1));
    return formatter.format(instant) + (inputTime.startsWith("-") ? " B.C." : "");
  }
}
