// /*
//  * SPDX-License-Identifier: Apache-2.0
//  */
//
// package org.hyperledger.fabric.samples.assettransfer;
//
// import static org.assertj.core.api.Assertions.assertThat;
// import static org.assertj.core.api.ThrowableAssert.catchThrowable;
// import static org.mockito.Mockito.inOrder;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.verifyNoInteractions;
// import static org.mockito.Mockito.when;
//
// import java.util.ArrayList;
// import java.util.Iterator;
// import java.util.List;
//
// import org.hyperledger.fabric.contract.Context;
// import org.hyperledger.fabric.shim.ChaincodeException;
// import org.hyperledger.fabric.shim.ChaincodeStub;
// import org.hyperledger.fabric.shim.ledger.KeyValue;
// import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;
// import org.mockito.InOrder;
//
// public final class FeedbackTransferTest {
//
//   private static final class MockKeyValue implements KeyValue {
//
//     private final String key;
//     private final String value;
//
//     MockKeyValue(final String key, final String value) {
//       super();
//       this.key = key;
//       this.value = value;
//     }
//
//     @Override
//     public String getKey() {
//       return this.key;
//     }
//
//     @Override
//     public String getStringValue() {
//       return this.value;
//     }
//
//     @Override
//     public byte[] getValue() {
//       return this.value.getBytes();
//     }
//
//   }
//
//   private static final class MockFeedbackResultsIterator implements QueryResultsIterator<KeyValue> {
//
//     private final List<KeyValue> feedbackList;
//
//     MockFeedbackResultsIterator() {
//       super();
//
//       feedbackList = new ArrayList<KeyValue>();
//
//       feedbackList.add(new MockKeyValue("f1",
//           "{\"opinion\":\"duzo lepiej\",\"feedbackID\":\"f1\",\"course\":\"math1\",\"professor\":\"profsup\"}"));
//       feedbackList.add(new MockKeyValue("f2",
//           "{\"opinion\":\"slabo\",\"feedbackID\":\"f2\",\"course\":\"math0.5\",\"professor\":\"profsup\"}"));
//     }
//
//     @Override
//     public Iterator<KeyValue> iterator() {
//       return feedbackList.iterator();
//     }
//
//     @Override
//     public void close() throws Exception {
//       // do nothing
//     }
//
//   }
//
//   @Test
//   public void invokeUnknownTransaction() {
//     FeedbackTransfer contract = new FeedbackTransfer();
//     Context ctx = mock(Context.class);
//
//     Throwable thrown = catchThrowable(() -> {
//       contract.unknownTransaction(ctx);
//     });
//
//     assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
//         .hasMessage("Undefined contract method called");
//     assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo(null);
//
//     verifyNoInteractions(ctx);
//   }
//
//   @Nested
//   class InvokeReadFeedbackTransaction {
//
//     @Test
//     public void whenFeedbackExists() {
//       FeedbackTransfer contract = new FeedbackTransfer();
//       Context ctx = mock(Context.class);
//       ChaincodeStub stub = mock(ChaincodeStub.class);
//       when(ctx.getStub()).thenReturn(stub);
//       when(stub.getStringState("feedback1"))
//           .thenReturn(
//               "{ \"feedbackID\": \"feedback1\", \"course\": \"math1\", \"professor\": \"prof1\", \"studentHash\": \"stuhash1\", \"opinion\": \"well prepared course\" }");
//
//       Feedback feedback = contract.ReadFeedback(ctx, "feedback1");
//
//       assertThat(feedback).isEqualTo(new Feedback("feedback1", "math1", "prof1", "well prepared course"));
//     }
//
//     @Test
//     public void whenFeedbackDoesNotExist() {
//       FeedbackTransfer contract = new FeedbackTransfer();
//       Context ctx = mock(Context.class);
//       ChaincodeStub stub = mock(ChaincodeStub.class);
//       when(ctx.getStub()).thenReturn(stub);
//       when(stub.getStringState("feedback1")).thenReturn("");
//
//       Throwable thrown = catchThrowable(() -> {
//         contract.ReadFeedback(ctx, "feedback1");
//       });
//
//       assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
//           .hasMessage("Feedback feedback1 does not exist");
//       assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("FEEDBACK_NOT_FOUND".getBytes());
//     }
//   }
//
//   @Test
//   void invokeInitLedgerTransaction() {
//     FeedbackTransfer contract = new FeedbackTransfer();
//     Context ctx = mock(Context.class);
//     ChaincodeStub stub = mock(ChaincodeStub.class);
//     when(ctx.getStub()).thenReturn(stub);
//
//     contract.InitLedger(ctx);
//
//     InOrder inOrder = inOrder(stub);
//     inOrder.verify(stub).putStringState("f1",
//         "{\"opinion\":\"duzo lepiej\",\"feedbackID\":\"f1\",\"course\":\"math1\",\"studentHash\":\"Tomokohash\",\"professor\":\"profsup\"}");
//     inOrder.verify(stub).putStringState("f2",
//         "{\"opinion\":\"slabo\",\"feedbackID\":\"f2\",\"course\":\"math0.5\",\"studentHash\":\"Tomokohash\",\"professor\":\"profsup\"}");
//
//   }
//
//   @Nested
//   class InvokeCreateFeedbackTransaction {
//
//     @Test
//     public void whenFeedbackExists() {
//       FeedbackTransfer contract = new FeedbackTransfer();
//       Context ctx = mock(Context.class);
//       ChaincodeStub stub = mock(ChaincodeStub.class);
//       when(ctx.getStub()).thenReturn(stub);
//       when(stub.getStringState("feedback1"))
//           .thenReturn(
//               "{ \"feedbackID\": \"feedback1\", \"course\": \"math1\", \"professor\": \"prof1\", \"opinion\": \"well prepared course\" }");
//
//       Throwable thrown = catchThrowable(() -> {
//         contract.CreateFeedback(ctx, "feedback1", "math1", "prof1", "nie wiem");
//       });
//
//       assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
//           .hasMessage("Feedback feedback1 already exists");
//       assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("FEEDBACK_ALREADY_EXISTS".getBytes());
//     }
//
//     @Test
//     public void whenFeedbackDoesNotExist() {
//       FeedbackTransfer contract = new FeedbackTransfer();
//       Context ctx = mock(Context.class);
//       ChaincodeStub stub = mock(ChaincodeStub.class);
//       when(ctx.getStub()).thenReturn(stub);
//       when(stub.getStringState("feedback1")).thenReturn("");
//
//       Feedback feedback = contract.CreateFeedback(ctx, "feedback1", "math1", "prof1",
//           "well prepared course");
//
//       assertThat(feedback).isEqualTo(new Feedback("feedback1", "math1", "prof1", "well prepared course"));
//     }
//   }
//
//   // @Test
//   // void invokeGetAllFeedbacksTransaction() {
//   // FeedbackTransfer contract = new FeedbackTransfer();
//   // Context ctx = mock(Context.class);
//   // ChaincodeStub stub = mock(ChaincodeStub.class);
//   // when(ctx.getStub()).thenReturn(stub);
//   // when(stub.getStateByRange("", "")).thenReturn(new
//   // MockFeedbackResultsIterator());
//   //
//   // String feedbacks = contract.GetAllFeedbacks(ctx);
//   //
//   // assertThat(feedbacks).isEqualTo(
//   // "[{\"opinion\":\"duzo
//   // lepiej\",\"feedbackID\":\"f1\",\"course\":\"math1\",\"studentHash\":\"Tomokohash\",\"professor\":\"profsup\"},"
//   // +
//   // "{\"opinion\":\"slabo\",\"feedbackID\":\"f2\",\"course\":\"math0.5\",\"studentHash\":\"Tomokohash\",\"professor\":\"profsup\"}]");
//   //
//   // }
//
//   @Nested
//   class UpdateFeedbackTransaction {
//
//     @Test
//     public void whenFeedbackExists() {
//       FeedbackTransfer contract = new FeedbackTransfer();
//       Context ctx = mock(Context.class);
//       ChaincodeStub stub = mock(ChaincodeStub.class);
//       when(ctx.getStub()).thenReturn(stub);
//       when(stub.getStringState("feedback1"))
//           .thenReturn(
//               "{ \"feedbackID\": \"feedback1\", \"course\": \"math1\", \"professor\": \"prof1\", \"opinion\": \"well prepared course\" }");
//
//       Feedback feedback = contract.UpdateFeedback(ctx, "feedback1", "math1", "prof1", "indeed");
//
//       assertThat(feedback).isEqualTo(new Feedback("feedback1", "math1", "prof1", "indeed"));
//     }
//   }
//
//   // @Test
//   // public void whenFeedbackDoesNotExist() {
//   // FeedbackTransfer contract = new FeedbackTransfer();
//   // Context ctx = mock(Context.class);
//   // ChaincodeStub stub = mock(ChaincodeStub.class);
//   // when(ctx.getStub()).thenReturn(stub);
//   // when(stub.getStringState("feedback1")).thenReturn("");
//   //
//   // Throwable thrown = catchThrowable(() -> {
//   // contract.TransferFeedback(ctx, "feedback1", "Alex");
//   // });
//   //
//   // assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
//   // .hasMessage("Feedback feedback1 does not exist");
//   // assertThat(((ChaincodeException)
//   // thrown).getPayload()).isEqualTo("FEEDBACK_NOT_FOUND".getBytes());
//   // }
//   // }
// }
