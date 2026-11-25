CREATE TABLE eval_questions (
                                id UUID PRIMARY KEY,
                                document_name TEXT NOT NULL,
                                question TEXT NOT NULL,
                                gold_start INTEGER NOT NULL,
                                gold_end INTEGER NOT NULL,
                                created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                CHECK (gold_start >= 0),
                                CHECK (gold_end > gold_start)
);

CREATE INDEX eval_questions_document_name_idx
    ON eval_questions(document_name);

CREATE TABLE experiments (
                             id UUID PRIMARY KEY,
                             document_name TEXT NOT NULL,
                             config JSONB NOT NULL,
                             metrics JSONB NOT NULL,
                             created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);