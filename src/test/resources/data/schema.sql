CREATE TABLE clients
(
    client_id bigint NOT NULL,
    client_name varchar2 varying NOT NULL,
    CONSTRAINT clients_pkey PRIMARY KEY (client_id)
);

CREATE TABLE transactions
(
    id bigint NOT NULL,
    year integer NOT NULL,
    month varchar2 NOT NULL,
    client_id bigint NOT NULL,
    amount double precision NOT NULL,
    "time" time without time zone NOT NULL,
    day integer NOT NULL,
    client_client_id bigint,
    CONSTRAINT "Transactions_pkey" PRIMARY KEY (id),
    CONSTRAINT fk_client_transaction FOREIGN KEY (client_id)
);
