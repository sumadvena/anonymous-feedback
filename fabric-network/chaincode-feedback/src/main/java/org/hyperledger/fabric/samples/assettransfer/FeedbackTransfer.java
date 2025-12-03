/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.owlike.genson.Genson;

@Contract(name = "basic", info = @Info(title = "Feedback Transfer", description = "The hyperlegendary asset transfer", version = "0.0.1-SNAPSHOT", license = @License(name = "Apache 2.0 License", url = "http://www.apache.org/licenses/LICENSE-2.0.html"), contact = @Contact(email = "a.transfer@example.com", name = "Adrian Transfer", url = "https://hyperledger.example.com")))
@Default
public final class FeedbackTransfer implements ContractInterface {

  private final Genson genson = new Genson();

  private enum FeedbackTransferErrors {
    FEEDBACK_NOT_FOUND,
    FEEDBACK_ALREADY_EXISTS
  }

  private enum CourseTransferErrors {
    COURSE_NOT_FOUND,
    COURSE_ALREADY_EXISTS,
    COURSE_ACCESS_DENIED,
    COURSE_MALICIOUS_HASH_CHANGE_DETECTED
  }

  public final static String sha256(final String input) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder(2 * hash.length);
      for (byte b : hash) {
        sb.append(String.format("%02x", b));
      }
      return sb.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  @Transaction(intent = Transaction.TYPE.SUBMIT)
  public Feedback CreateFeedback(final Context ctx, final String feedbackID,
      final String timestamp,
      final String course,
      final String professor,
      final String opinionBase64,
      final String wholeFeedbackHashUser) {

    // maybe redundant?
    if (FeedbackExists(ctx, feedbackID)) {
      String errorMessage = String.format("Feedback %s already exists", feedbackID);
      System.out.println(errorMessage);
      throw new ChaincodeException(errorMessage, FeedbackTransferErrors.FEEDBACK_ALREADY_EXISTS.toString());
    }

    CheckIfStudentCanWrite(ctx, course, feedbackID);

    return putFeedback(ctx, new Feedback(feedbackID, timestamp, course, professor, opinionBase64),
        wholeFeedbackHashUser);
  }

  private Feedback putFeedback(final Context ctx, final Feedback feedback, final String wholeFeedbackHashUser) {
    String sortedJson = genson.serialize(feedback);
    ctx.getStub().putStringState(feedback.getFeedbackID(), sortedJson);

    OverrideStudentsFeedbacksHash(ctx, feedback, wholeFeedbackHashUser);

    return feedback;
  }

  @Transaction(intent = Transaction.TYPE.EVALUATE)
  public Feedback ReadFeedback(final Context ctx, final String feedbackID) {
    String feedbackJSON = ctx.getStub().getStringState(feedbackID);

    if (feedbackJSON == null || feedbackJSON.isEmpty()) {
      String errorMessage = String.format("Feedback %s does not exist", feedbackID);
      System.out.println(errorMessage);
      throw new ChaincodeException(errorMessage, FeedbackTransferErrors.FEEDBACK_NOT_FOUND.toString());
    }

    return genson.deserialize(feedbackJSON, Feedback.class);
  }

  @Transaction(intent = Transaction.TYPE.EVALUATE)
  public boolean FeedbackExists(final Context ctx, final String feedbackID) {
    String feedbackJSON = ctx.getStub().getStringState(feedbackID);
    return (feedbackJSON != null && !feedbackJSON.isEmpty());
  }

  @Transaction(intent = Transaction.TYPE.EVALUATE)
  public String GetAllFeedbacks(final Context ctx) {
    ChaincodeStub stub = ctx.getStub();

    List<Feedback> queryResults = new ArrayList<>();
    QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

    for (KeyValue result : results) {
      Feedback feedback = genson.deserialize(result.getStringValue(), Feedback.class);
      System.out.println(feedback);
      queryResults.add(feedback);
    }

    return genson.serialize(queryResults);
  }

  @Transaction(intent = Transaction.TYPE.EVALUATE)
  public String GetAllFeedbacksHeaders(final Context ctx) {
    ChaincodeStub stub = ctx.getStub();

    List<java.util.Map<String, String>> queryResults = new ArrayList<>();
    QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

    for (KeyValue result : results) {
      Feedback feedback = genson.deserialize(result.getStringValue(), Feedback.class);
      java.util.Map<String, String> header = new java.util.HashMap<>();
      header.put("feedbackID", feedback.getFeedbackID());
      header.put("course", feedback.getCourse());
      header.put("professor", feedback.getProfessor());
      queryResults.add(header);
    }

    return genson.serialize(queryResults);
  }

  @Transaction(intent = Transaction.TYPE.EVALUATE)
  public String GetAllFeedbacksByProfessor(final Context ctx, final String professor) {
    ChaincodeStub stub = ctx.getStub();

    List<Feedback> queryResults = new ArrayList<>();
    QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

    for (KeyValue result : results) {
      Feedback feedback = genson.deserialize(result.getStringValue(), Feedback.class);
      if (professor != null && professor.equals(feedback.getProfessor())) {
        System.out.println(feedback);
        queryResults.add(feedback);
      }
    }

    return genson.serialize(queryResults);
  }

  @Transaction(intent = Transaction.TYPE.EVALUATE)
  public String GetAllFeedbackIDs(final Context ctx) {
    ChaincodeStub stub = ctx.getStub();

    List<String> queryResults = new ArrayList<>();
    QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

    for (KeyValue result : results) {
      Feedback feedback = genson.deserialize(result.getStringValue(), Feedback.class);

      System.out.println(feedback.getFeedbackID());
      queryResults.add(feedback.getFeedbackID());
    }

    return genson.serialize(queryResults);
  }

  @Transaction(intent = Transaction.TYPE.SUBMIT)
  public Course CreateCourse(final Context ctx, final String courseName, final String studentsIds[]) {
    String courseID = sha256(courseName);

    if (CourseExists(ctx, courseID)) {
      String errorMessage = String.format("Course %s already exists", courseID);
      System.out.println(errorMessage);
      throw new ChaincodeException(errorMessage, CourseTransferErrors.COURSE_ALREADY_EXISTS.toString());
    }

    Map<String, String> courseMap = new HashMap<>();
    for (String studentId : studentsIds) {
      courseMap.put(sha256(courseName.concat(studentId)), "");
    }

    return putCourse(ctx, new Course(courseID, courseMap));
  }

  private Course putCourse(final Context ctx, final Course course) {
    String sortedJson = genson.serialize(course);
    ctx.getStub().putStringState(course.getCourseID(), sortedJson);
    return course;
  }

  @Transaction(intent = Transaction.TYPE.EVALUATE)
  public Course ReadCourse(final Context ctx, final String courseID) {
    String courseJSON = ctx.getStub().getStringState(courseID);

    if (courseJSON == null || courseJSON.isEmpty()) {
      String errorMessage = String.format("Course %s does not exist", courseID);
      System.out.println(errorMessage);
      throw new ChaincodeException(errorMessage, CourseTransferErrors.COURSE_NOT_FOUND.toString());
    }

    return genson.deserialize(courseJSON, Course.class);
  }

  // when student did not write a feedback and if the network got the same
  // feedback as has been sent,
  // overwrite the "" - the whole feedback hash
  @Transaction(intent = Transaction.TYPE.SUBMIT)
  public Course OverrideStudentsFeedbacksHash(final Context ctx, final Feedback feedback,
      final String wholeFeedbackHashUser) {

    final String courseID = sha256(feedback.getCourse());

    final String wholeFeedbackHashNetwork = sha256(feedback.toString());

    Course course = ReadCourse(ctx, courseID);
    Map<String, String> courseMap = course.getCourseMap();

    if (wholeFeedbackHashUser != null
        && wholeFeedbackHashUser.equals(wholeFeedbackHashNetwork)) {

      courseMap.put(feedback.getFeedbackID(), wholeFeedbackHashNetwork);
      return putCourse(ctx, new Course(courseID, courseMap));

    } else {
      String errorMessage = String.format("Could not override hash - malicious hash change detected", courseID);
      System.out.println(errorMessage);
      throw new ChaincodeException(errorMessage, CourseTransferErrors.COURSE_MALICIOUS_HASH_CHANGE_DETECTED.toString());
    }
  }

  @Transaction(intent = Transaction.TYPE.EVALUATE)
  public boolean CourseExists(final Context ctx, final String courseID) {
    String courseJSON = ctx.getStub().getStringState(courseID);
    return (courseJSON != null && !courseJSON.isEmpty());
  }

  @Transaction(intent = Transaction.TYPE.EVALUATE)
  public boolean CheckIfStudentCanWrite(final Context ctx, final String courseName, final String feedbackID) {

    final String courseID = sha256(courseName);
    Course course = ReadCourse(ctx, courseID);
    Map<String, String> courseMap = course.getCourseMap();
    String wholeFeedbackHash = courseMap.get(feedbackID);

    if (wholeFeedbackHash != null && wholeFeedbackHash.equals("")) {
      return true;
    }

    String errorMessage = String.format("Access for this course is not granted for this student");
    System.out.println("Access for this course is not granted for this student");
    throw new ChaincodeException(errorMessage, CourseTransferErrors.COURSE_ACCESS_DENIED.toString());
  }

  @Transaction(intent = Transaction.TYPE.EVALUATE)
  public boolean CheckIfFeedbackUnchanged(final Context ctx, final String courseID, final String feedbackID,
      final String wholeFeedbackHashUser) {

    String courseJSON = ctx.getStub().getStringState(courseID);

    if (courseJSON == null || courseJSON.isEmpty()) {
      String errorMessage = String.format("Course %s does not exist", courseID);
      System.out.println(errorMessage);
      throw new ChaincodeException(errorMessage, CourseTransferErrors.COURSE_NOT_FOUND.toString());
    }

    Course course = genson.deserialize(courseJSON, Course.class);
    Map<String, String> courseMap = course.getCourseMap();
    String wholeFeedbackHash = courseMap.get(feedbackID);

    if (wholeFeedbackHash != null && wholeFeedbackHash.equals(wholeFeedbackHashUser)) {
      return true;
    } else {
      return false;
    }
  }

  @Transaction(intent = Transaction.TYPE.EVALUATE)
  public String GetAllCourses(final Context ctx) {
    ChaincodeStub stub = ctx.getStub();

    List<Course> queryResults = new ArrayList<>();
    QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

    for (KeyValue result : results) {
      Course course = genson.deserialize(result.getStringValue(), Course.class);
      System.out.println(course);
      queryResults.add(course);
    }

    return genson.serialize(queryResults);
  }
}
