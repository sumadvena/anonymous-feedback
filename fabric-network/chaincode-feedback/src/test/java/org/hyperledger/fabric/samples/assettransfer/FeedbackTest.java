/*
 * SPDX-License-Identifier: Apache-2.0
 */
//
// package org.hyperledger.fabric.samples.assettransfer;
//
// import static org.assertj.core.api.Assertions.assertThat;
//
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;
//
// public final class FeedbackTest {
//
//     @Nested
//     class Equality {
//
//         @Test
//         public void isReflexive() {
//             Feedback feedback = new Feedback("feedback1", "math1", "prof1", "well prepared course");
//
//             assertThat(feedback).isEqualTo(feedback);
//         }
//
//         @Test
//         public void isSymmetric() {
//             Feedback feedbackA = new Feedback("feedback1", "math1", "prof1", "well prepared course");
//             Feedback feedbackB = new Feedback("feedback1", "math1", "prof1", "well prepared course");
//
//             assertThat(feedbackA).isEqualTo(feedbackB);
//             assertThat(feedbackB).isEqualTo(feedbackA);
//         }
//
//         @Test
//         public void isTransitive() {
//             Feedback feedbackA = new Feedback("feedback1", "math1", "prof1", "well prepared course");
//             Feedback feedbackB = new Feedback("feedback1", "math1", "prof1", "well prepared course");
//             Feedback feedbackC = new Feedback("feedback1", "math1", "prof1", "well prepared course");
//
//             assertThat(feedbackA).isEqualTo(feedbackB);
//             assertThat(feedbackB).isEqualTo(feedbackC);
//             assertThat(feedbackA).isEqualTo(feedbackC);
//         }
//
//         @Test
//         public void handlesInequality() {
//             Feedback feedbackA = new Feedback("feedback1", "math1", "prof1", "well prepared course");
//             Feedback feedbackB = new Feedback("feedback2", "math1.1", "prof2", "even better prepared course");
//
//             assertThat(feedbackA).isNotEqualTo(feedbackB);
//         }
//
//         @Test
//         public void handlesOtherObjects() {
//             Feedback feedbackA = new Feedback("feedback1", "math1", "prof1", "well prepared course");
//             String feedbackB = "not a feedback";
//
//             assertThat(feedbackA).isNotEqualTo(feedbackB);
//         }
//
//         @Test
//         public void handlesNull() {
//             Feedback feedback = new Feedback("feedback1", "math1", "prof1", "well prepared course");
//
//             assertThat(feedback).isNotEqualTo(null);
//         }
//     }
//
//     // @Test
//     // public void toStringIdentifiesFeedback() {
//     //     Feedback feedback = new Feedback("feedback1", "math1", "prof1", "good");
//     //
//     //     assertThat(feedback.toString()).isEqualTo("Feedback@e04f6c53 [feedbackID=feedback1, course=math1, professor=prof1, studentHash=stuhash1, opinion=good]");
//     // }
// }
