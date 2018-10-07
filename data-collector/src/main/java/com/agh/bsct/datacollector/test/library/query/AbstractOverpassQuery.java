package com.agh.bsct.datacollector.test.library.query;

abstract class AbstractOverpassQuery {
    protected com.agh.bsct.datacollector.test.library.query.OverpassQueryBuilder builder;

    AbstractOverpassQuery() {
        this(new OverpassQueryBuilderImpl());
    }

    AbstractOverpassQuery(com.agh.bsct.datacollector.test.library.query.OverpassQueryBuilder builder) {
        this.builder = builder;
    }

    public void onSubQueryResult(AbstractOverpassSubQuery subQuery) {
        builder.append(subQuery.build());
    }

    protected abstract String build();
}
