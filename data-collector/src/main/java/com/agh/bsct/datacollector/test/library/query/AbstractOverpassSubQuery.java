package com.agh.bsct.datacollector.test.library.query;

abstract class AbstractOverpassSubQuery extends com.agh.bsct.datacollector.test.library.query.AbstractOverpassQuery {
    private com.agh.bsct.datacollector.test.library.query.OverpassQuery parent;

    public AbstractOverpassSubQuery(com.agh.bsct.datacollector.test.library.query.OverpassQuery parent) {
        super();
        this.parent = parent;
    }

    AbstractOverpassSubQuery(com.agh.bsct.datacollector.test.library.query.OverpassQuery parent, com.agh.bsct.datacollector.test.library.query.OverpassQueryBuilder builder) {
        super(builder);
        this.parent = parent;
    }

    public final OverpassQuery end() {
        parent.onSubQueryResult(this);

        return parent;
    }
}
