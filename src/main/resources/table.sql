-- 创建数据库模板
create table vector_store
(
    id        uuid default uuid_generate_v4() not null
        primary key,
    content   text,
    metadata  json,
    embedding vector(1536)
);

alter table vector_store
    owner to wanfeng;

create index spring_ai_vector_index
    on vector_store using hnsw (embedding vector_cosine_ops);

