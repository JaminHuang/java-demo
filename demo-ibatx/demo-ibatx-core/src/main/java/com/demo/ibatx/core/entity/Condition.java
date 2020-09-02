package com.demo.ibatx.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.*;

/**
 * 查询条件
 */
public class Condition<T> implements Serializable {

    private static final long serialVersionUID = -804499068048856102L;

    private static final int DEFAULT_ROW = 10;

    private HashMap<String, Object> params;

    private List<Criteria> criteriaList;

    private List<OrderBy> orderByList;

    private Set<String> fieldsNameSet;

    private Set<String> excludeFieldsNameSet;

    private LimitCondition limitCondition;

    private boolean ignoreLogicDelete;

    private int paramIndex = 0;

    private boolean forceMaster;

    public Condition() {
        this.params = new HashMap<>();
        this.criteriaList = new ArrayList<>();
        this.orderByList = new ArrayList<>();
        this.limitCondition = new LimitCondition(0, DEFAULT_ROW);
    }

    @JsonIgnore
    public Set<String> getFieldsNames() {
        return fieldsNameSet;
    }

    @JsonIgnore
    public void setFieldsNames(String... fieldsNames) {
        this.fieldsNameSet = new HashSet<>(Arrays.asList(fieldsNames));
    }

    @JsonIgnore
    public Set<String> getExcludeFieldsNames() {
        return excludeFieldsNameSet;
    }

    @JsonIgnore
    public void setExcludeFieldsNames(String... excludeFieldsNames) {
        this.excludeFieldsNameSet = new HashSet<>(Arrays.asList(excludeFieldsNames));
    }

    public void setFieldsNameSet(Set<String> fieldsNameSet) {
        this.fieldsNameSet = fieldsNameSet;
    }

    public void setExcludeFieldsNameSet(Set<String> excludeFieldsNameSet) {
        this.excludeFieldsNameSet = excludeFieldsNameSet;
    }

    public void ignoreLogicDelete() {
        this.ignoreLogicDelete = true;
    }

    public boolean isIgnoreLogicDelete() {
        return ignoreLogicDelete;
    }

    public void forceMaster() {
        this.forceMaster = true;
    }

    public boolean isforceMaster() {
        return this.forceMaster;
    }

    public Criteria createCriteria() {
        Criteria criteria = new Criteria(this.generateParamIndex());
        this.criteriaList.add(criteria);
        return criteria;
    }

    public Criteria orCriteria() {
        if (this.criteriaList.isEmpty()) {
            return createCriteria();
        }
        Criteria criteria = new Criteria(ParamRelEnum.OR.getValue(), this.generateParamIndex());
        this.criteriaList.add(criteria);
        return criteria;
    }

    public Criteria andCriteria() {
        if (this.criteriaList.isEmpty()) {
            return createCriteria();
        }
        Criteria criteria = new Criteria(ParamRelEnum.AND.getValue(), this.generateParamIndex());
        this.criteriaList.add(criteria);
        return criteria;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public List<Criteria> getCriteriaList() {
        return criteriaList;
    }

    public List<OrderBy> getOrderByList() {
        return orderByList;
    }

    /**
     * 默认是正序
     *
     * @param field 字段名
     * @return 排序条件
     */
    public OrderCriteria setOrderBy(String field) {
        return new OrderCriteria(field);
    }

    public void limit(int num, int row) {
        this.limitCondition = new PageCondition(row, num);
    }

    public void limit(int row) {
        this.limitCondition = new PageCondition(row, 1);
    }

    public LimitCondition getLimitCondition() {
        return limitCondition;
    }

    public boolean isHasCondition() {
        return !criteriaList.isEmpty();
    }

    private int generateParamIndex() {
        return paramIndex++;
    }

    private void addParam(String paramPlaceN, Object paramV) {
        this.params.put(paramPlaceN, paramV);
    }

    public class OrderCriteria implements Serializable {

        private static final long serialVersionUID = 7279799697254129137L;
        private OrderBy orderBy;

        private OrderCriteria(String field) {
            this.orderBy = new OrderBy(field);
            orderByList.add(orderBy);
        }

        /**
         * 默认是正序
         *
         * @param field 字段名
         * @return 排序条件
         */
        public OrderCriteria andOrderBy(String field) {
            return new OrderCriteria(field);
        }

        public OrderCriteria desc() {
            orderBy.desc();
            return this;
        }

    }

    /**
     * 条件
     */
    public static class Criteria implements Serializable {

        private static final long serialVersionUID = 5527463636589920046L;
        private String criRel;

        private int index;

        private int paramIndex;

        private List<ParamCondition> paramConditions;

        public Criteria() {
        }

        private Criteria(int index) {
            this.index = index;
            this.paramConditions = new ArrayList<>();
        }

        private Criteria(String criRel, int index) {
            this.criRel = criRel;
            this.index = index;
            this.paramConditions = new ArrayList<>();
        }

        public Criteria andEqual(String paramN, Object paramV) {
            addParamCondition(paramN, paramV, ParamSymbol.EQUAL, paramV);
            return this;
        }

        public Criteria orEqual(String paramN, Object paramV) {
            orParamCondition(paramN, paramV, ParamSymbol.EQUAL, paramV);
            return this;
        }

        public Criteria andNotEqual(String paramN, Object paramV) {
            addParamCondition(paramN, paramV, ParamSymbol.NOT_EQUAL, paramV);
            return this;
        }

        public Criteria andIn(String paramN, Collection collection) {
            addParamCondition(paramN, collection, ParamSymbol.IN, collection);
            return this;
        }

        public Criteria orIn(String paramN, Collection collection) {
            orParamCondition(paramN, collection, ParamSymbol.IN, collection);
            return this;
        }

        public Criteria andNotIn(String paramN, Collection collection) {
            addParamCondition(paramN, collection, ParamSymbol.NOT_IN, collection);
            return this;
        }

        public Criteria andIsNull(String paramN) {
            addParamCondition(paramN, ParamSymbol.IS_NULL);
            return this;
        }

        public Criteria andIsNotNull(String paramN) {
            addParamCondition(paramN, ParamSymbol.IS_NOT_NULL);
            return this;
        }

        public Criteria andLike(String paramN, Object paramV) {
            addParamCondition(paramN, paramV, ParamSymbol.LIKE, paramV);
            return this;
        }

        public Criteria andLessThan(String paramN, Object paramV) {
            addParamCondition(paramN, paramV, ParamSymbol.LT, paramV);
            return this;
        }

        public Criteria orLessThan(String paramN, Object paramV) {
            orParamCondition(paramN, paramV, ParamSymbol.LT, paramV);
            return this;
        }

        public Criteria andLessThanEqual(String paramN, Object paramV) {
            addParamCondition(paramN, paramV, ParamSymbol.LTE, paramV);
            return this;
        }

        public Criteria orLessThanEqual(String paramN, Object paramV) {
            orParamCondition(paramN, paramV, ParamSymbol.LTE, paramV);
            return this;
        }

        public Criteria andGreaterThan(String paramN, Object paramV) {
            addParamCondition(paramN, paramV, ParamSymbol.GT, paramV);
            return this;
        }

        public Criteria orGreaterThan(String paramN, Object paramV) {
            orParamCondition(paramN, paramV, ParamSymbol.GT, paramV);
            return this;
        }

        public Criteria andGreaterThanEqual(String paramN, Object paramV) {
            addParamCondition(paramN, paramV, ParamSymbol.GTE, paramV);
            return this;
        }

        public Criteria orGreaterThanEqual(String paramN, Object paramV) {
            orParamCondition(paramN, paramV, ParamSymbol.GTE, paramV);
            return this;
        }

        private void addParamCondition(String paramN, Object paramV, ParamSymbol symbol, Object value) {
            if (Objects.isNull(paramV)) {
                return;
            }
            ParamCondition paramCondition = new ParamCondition(this.index, paramIndex++, paramN, symbol, value);
            paramConditions.add(paramCondition);
        }

        private void orParamCondition(String paramN, Object paramV, ParamSymbol symbol, Object value) {
            if (Objects.isNull(paramV)) {
                return;
            }
            ParamCondition paramCondition = new ParamCondition(ParamRelEnum.OR, this.index, paramIndex++, paramN, symbol, value);
            paramConditions.add(paramCondition);
        }

        private void addParamCondition(String paramN, ParamSymbol symbol) {
            paramConditions.add(new ParamCondition(this.index, paramIndex++, paramN, symbol, null));

        }

        public String getCriRel() {
            return criRel;
        }

        public int getIndex() {
            return index;
        }

        public List<ParamCondition> getParamConditions() {
            return paramConditions;
        }

        public void setCriRel(String criRel) {
            this.criRel = criRel;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getParamIndex() {
            return paramIndex;
        }

        public void setParamIndex(int paramIndex) {
            this.paramIndex = paramIndex;
        }

        public void setParamConditions(List<ParamCondition> paramConditions) {
            this.paramConditions = paramConditions;
        }

        @Override
        public String toString() {
            StringBuilder criteriaStr = new StringBuilder();
            criteriaStr.append("Criteria(")
                    .append("criRel=").append(criRel).append(", index=").append(index);
            criteriaStr.append(" params = [");
            if (Objects.nonNull(paramConditions) && !paramConditions.isEmpty()) {
                for (ParamCondition paramCondition : paramConditions) {
                    criteriaStr.append(paramCondition).append(";");
                }
            }
            criteriaStr.append(" ]");
            criteriaStr.append(")");
            return criteriaStr.toString();
        }

    }

    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder();
        toString.append("Condition{ ");
        if (Objects.nonNull(fieldsNameSet)) {
            toString.append("fieldsNames=").append(fieldsNameSet).append(", ");
        }
        if (Objects.nonNull(excludeFieldsNameSet)) {
            toString.append("excludeFieldsNames=").append(excludeFieldsNameSet).append(", ");
        }
        toString.append("ignoreLogicDelete=").append(ignoreLogicDelete).append(", ");
        toString.append("forceMaster=").append(forceMaster).append(", ");
        if (Objects.nonNull(orderByList) && !orderByList.isEmpty()) {
            toString.append("orderByList=").append(orderByList).append(", ");
        }
        if (Objects.nonNull(criteriaList) && !criteriaList.isEmpty()) {
            toString.append(criteriaList);
        }
        toString.append(",").append(limitCondition);
        toString.append("}");
        return toString.toString();
    }

}
