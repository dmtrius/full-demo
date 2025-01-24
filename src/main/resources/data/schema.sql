DROP TABLE IF EXISTS public.clients;

CREATE TABLE IF NOT EXISTS public.clients
(
    client_id bigint NOT NULL,
    client_name character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT clients_pkey PRIMARY KEY (client_id)
);

DROP TABLE IF EXISTS public.transactions;

CREATE TABLE IF NOT EXISTS public.transactions
(
    id bigint NOT NULL,
    year integer NOT NULL,
    month character varying COLLATE pg_catalog."default" NOT NULL,
    client_id bigint NOT NULL,
    amount double precision NOT NULL,
    "time" time without time zone NOT NULL,
    day integer NOT NULL,
    client_client_id bigint,
    CONSTRAINT "Transactions_pkey" PRIMARY KEY (id),
    CONSTRAINT fk_client_transaction FOREIGN KEY (client_id)
        REFERENCES public.clients (client_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);
