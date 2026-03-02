# Пример поискового запроса и индексы

## Пример поискового запроса

Поисковый запрос (фильтр по статусу, автору и интервалу дат, сортировка по дате):

```sql
EXPLAIN (ANALYZE, BUFFERS)
SELECT d.id,
       d.uid,
       d.title,
       d.status,
       d.author,
       d.created_at
FROM document d
WHERE d.status = 'SUBMITTED'
  AND d.author = 'generator'
  AND d.created_at BETWEEN TIMESTAMP '2026-03-01 00:00:00'
                      AND TIMESTAMP '2026-03-04 00:00:00'
ORDER BY d.created_at DESC
LIMIT 50;
```
Фактический план выполнения:
```sql
 Limit  (cost=15.96..15.99 rows=10 width=121) (actual time=0.105..0.110 rows=50 loops=1)
   Buffers: shared hit=10
   ->  Sort  (cost=15.96..15.99 rows=10 width=121) (actual time=0.104..0.106 rows=50 loops=1)
         Sort Key: created_at DESC
         Sort Method: quicksort  Memory: 43kB
         Buffers: shared hit=10
         ->  Bitmap Heap Scan on document d  (cost=4.35..15.80 rows=10 width=121) (actual time=0.030..0.055 rows=70 loops=1)
               Recheck Cond: ((status)::text = 'SUBMITTED'::text)
               Filter: ((created_at >= '2026-03-01 00:00:00'::timestamp without time zone) AND (created_at <= '2026-03-04 00:00:00'::timestamp without time zone) AND ((author)::text = 'generator'::text))
               Heap Blocks: exact=4
               Buffers: shared hit=7
               ->  Bitmap Index Scan on idx_document_status_id  (cost=0.00..4.35 rows=10 width=0) (actual time=0.019..0.019 rows=95 loops=1)
                     Index Cond: ((status)::text = 'SUBMITTED'::text)
                     Buffers: shared hit=3
 Planning:
   Buffers: shared hit=197
 Planning Time: 0.948 ms
 Execution Time: 0.175 ms

```

- использует индекс `idx_document_status_id(status, id)` для отбора всех документов со статусом `SUBMITTED`;
- выполняет `Bitmap Heap Scan` по таблице `document` с фильтром по интервалу дат `created_at BETWEEN ...` и автору `author = 'generator'`;
- по результатам скана сортирует отфильтрованные строки по `created_at DESC` (узел `Sort`, метод `quicksort`, всё в памяти);
- оператор `LIMIT 50` обрезает уже отсортированный набор до нужного количества строк;
- чтение данных идёт только из буфера (`Buffers: shared hit=...`), без обращений к диску;
- общее время выполнения запроса - порядка 0.2 ms на текущем объёме данных.

## Пару слов про индексы

1. Для ускорения работы воркеров был создан отдельный индекс поиска по статусу и id:
```xml
<changeSet id="1-add-index-document-status-id" author="a.zharov">
        <createIndex indexName="idx_document_status_id"
                     tableName="document">
            <column name="status"/>
            <column name="id"/>
        </createIndex>
    </changeSet>
```

2. Для поискового запроса из примера был создан отдельный индекс по статусу + дате:
```xml
<changeSet id="2-add-index-document-status-created-at" author="a.zharov">
    <createIndex indexName="idx_document_status_created_at"
                 tableName="document">
        <column name="status"/>
        <column name="created_at"/>
    </createIndex>
</changeSet> 
```