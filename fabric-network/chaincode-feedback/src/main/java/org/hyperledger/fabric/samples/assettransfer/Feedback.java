/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Feedback {

  @Property()
  private final String feedbackID;

  @Property()
  private final String timestamp;

  @Property()
  private final String course;

  @Property()
  private final String professor;

  @Property()
  private final String opinion;

  public String getFeedbackID() {
    return feedbackID;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public String getCourse() {
    return course;
  }

  public String getProfessor() {
    return professor;
  }

  public String getOpinion() {
    return opinion;
  }

  public Feedback(@JsonProperty("feedbackID") final String feedbackID,
      @JsonProperty("timestamp") final String timestamp,
      @JsonProperty("course") final String course,
      @JsonProperty("professor") final String professor,
      @JsonProperty("opinion") final String opinion) {
    this.feedbackID = feedbackID;
    this.timestamp = timestamp;
    this.course = course;
    this.professor = professor;
    this.opinion = opinion;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }

    Feedback other = (Feedback) obj;

    return Objects.equals(getFeedbackID(), other.getFeedbackID())
        && Objects.equals(getTimestamp(), other.getTimestamp())
        && Objects.equals(getProfessor(), other.getProfessor())
        && Objects.equals(getCourse(), other.getCourse())
        && Objects.equals(getOpinion(), other.getOpinion());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getFeedbackID(), getTimestamp(), getCourse(), getProfessor(), getOpinion());
  }

  @Override
  public String toString() {
    return feedbackID + timestamp + course + professor + opinion;
  }
}
