package com.example.demo.repository;

import com.example.demo.entity.Clients;
import com.example.demo.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Long> {
    @Query(value =
//            """
//                from Clients c join Transactions t
//                on c.clientId = t.clientId where t.month = 'January'
//            """,
            """
            select * from clients c
            left join transactions t
            on c.client_id = t.client_id
            where t.month = 'January'
            """,
            nativeQuery = true)
   List<Object> testSql1();

    //2. Count number of transaction for each month by John Snow
    @Query(value= """
            select t.month, count(t.id) from transactions t
            left join clients c on t.client_id = c.client_id
            group by t.month, c.client_name
            having c.client_name ='John Snow'
            """,
            nativeQuery = true)
    List<Map<String, Integer>> testSql2();

//3. Show months where the balance of transactions is positive for John snow
    @Query(value = """
            select t.month from transactions t
             left join clients c on t.client_id = c.client_id
             group by t.month, client_name
              having sum(t.amount) > 0
              and c.client_name = 'John Snow';
            """,
            nativeQuery = true)
    List<String> testSql3();
}
