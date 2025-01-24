INSERT INTO public.clients (client_id, client_name)
    VALUES (1, 'John Snow');
INSERT INTO public.clients (client_id, client_name)
    VALUES (2, 'Rob Stark');
INSERT INTO public.clients (client_id, client_name)
    VALUES (3, 'Sansa Stark');
INSERT INTO public.clients (client_id, client_name)
    VALUES (4, 'Ariya Stark');

INSERT INTO public.transactions(id, client_id, year, month, day, "time", amount, client_client_id)
        VALUES (1, 1, 2024, 'January', 3, '05:22:00 PM', -123.17, 1);
INSERT INTO public.transactions(id, client_id, year, month, day, "time", amount, client_client_id)
        VALUES (2, 1, 2024, 'January', 22, '10:05:00 AM', -66, 1);
INSERT INTO public.transactions(id, client_id, year, month, day, "time", amount, client_client_id)
        VALUES (3, 1, 2024, 'February', 1,'11:10:00 AM', 3500, 1);
INSERT INTO public.transactions(id, client_id, year, month, day, "time", amount, client_client_id)
        VALUES (4, 1, 2024, 'February', 17,'04:00:00 PM', -599, 1);
INSERT INTO public.transactions(id, client_id, year, month, day, "time", amount, client_client_id)
        VALUES (5, 1, 2024, 'March', 1, '11:00:00 AM', 3500, 1);
INSERT INTO public.transactions(id, client_id, year, month, day, "time", amount, client_client_id)
        VALUES (6, 1, 2024, 'March', 2, '07:00:00 PM', -2000, 1);
INSERT INTO public.transactions(id, client_id, year, month, day, "time", amount, client_client_id)
        VALUES (7, 1, 2024, 'March', 31, '06:17:00 PM', -2500, 1);
INSERT INTO public.transactions(id, client_id, year, month, day, "time", amount, client_client_id)
        VALUES (8, 2, 2024, 'January', 3, '07:22:00 AM', -17, 2);
INSERT INTO public.transactions(id, client_id, year, month, day, "time", amount, client_client_id)
        VALUES (9, 2, 2024, 'January', 26, '04:17:00 PM', -127, 2);
INSERT INTO public.transactions(id, client_id, year, month, day, "time", amount, client_client_id)
        VALUES (10, 2, 2024, 'February', 1, '11:01:00 AM', 2500, 2);
