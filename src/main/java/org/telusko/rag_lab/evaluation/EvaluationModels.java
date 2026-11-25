package org.telusko.rag_lab.evaluation;

import java.util.List;
import java.util.UUID;

/***
 * Save test questions: AddQuestionRequest saves a question and its [goldStart, goldEnd] span into eval_questions, returning AddQuestionResponse(id).
 *
 * Trigger a benchmark run: RunRequest(documentName, topK) fetches all rows for that documentName from eval_questions and maps them into EvalQuestion records.
 *
 * Retrieve & measure per question: For each EvalQuestion, your retriever searches for the topK closest chunks—returning them as Hit(startOffset, endOffset, distance)—while recording embedding, search, and total durations in Latencies.
 *
 * Grade, save & return: Your evaluator compares every Hit's [startOffset, endOffset] against EvalQuestion's [goldStart, goldEnd] to compute recallAt5, mrr, and ndcgAt5, aggregates p50/p95 latencies from Latencies, saves the run to experiments, and returns RunResponse.
 */

public class EvaluationModels {
    //save the qs into the eval_question database
    public record AddQuestionRequest(
            String documentName,
            String question,
            int goldStart,
            int goldEnd) {}
    //verified that qs has been saved by the backend and returns an id for the qs
    public record AddQuestionResponse(UUID id) {}

    //send the document name and top k embedded chunks for the pre saved queries in the database
    public record RunRequest(
            String documentName,
            int topK) {}

    //EvalQuestion + List<Hits> + Latencies to give a full report and save in "experiments" table
    public record RunResponse(
            UUID experimentId,
            String documentName,
            int questionCount,
            double recallAt5,
            double mrr,
            double ndcgAt5,
            double embedP50Ms,
            double embedP95Ms,
            double searchP50Ms,
            double searchP95Ms,
            double totalP50Ms,
            double totalP95Ms) {}
    //once the RunRequest is executed the Eval qs of the doc name is run
    public record EvalQuestion(
            UUID id,
            String documentName,
            String question,
            int goldStart,
            int goldEnd) {}
    //collect the response data
    public record Hit(
            int startOffset,
            int endOffset,
            double distance) {}
    //save the latencies of the response
    public record Latencies(
            List<Double> embedMs,
            List<Double> searchMs,
            List<Double> totalMs) {}
}


