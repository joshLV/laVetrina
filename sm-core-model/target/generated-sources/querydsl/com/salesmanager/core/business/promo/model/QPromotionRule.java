package com.salesmanager.core.business.promo.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QPromotionRule is a Querydsl query type for PromotionRule
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QPromotionRule extends EntityPathBase<PromotionRule> {

    private static final long serialVersionUID = 444507964L;

    public static final QPromotionRule promotionRule = new QPromotionRule("promotionRule");

    public final com.salesmanager.core.business.generic.model.QSalesManagerEntity _super = new com.salesmanager.core.business.generic.model.QSalesManagerEntity(this);

    public final ListPath<com.salesmanager.core.business.catalog.product.model.manufacturer.Manufacturer, com.salesmanager.core.business.catalog.product.model.manufacturer.QManufacturer> brands = this.<com.salesmanager.core.business.catalog.product.model.manufacturer.Manufacturer, com.salesmanager.core.business.catalog.product.model.manufacturer.QManufacturer>createList("brands", com.salesmanager.core.business.catalog.product.model.manufacturer.Manufacturer.class, com.salesmanager.core.business.catalog.product.model.manufacturer.QManufacturer.class, PathInits.DIRECT2);

    public final ListPath<com.salesmanager.core.business.catalog.category.model.Category, com.salesmanager.core.business.catalog.category.model.QCategory> categories = this.<com.salesmanager.core.business.catalog.category.model.Category, com.salesmanager.core.business.catalog.category.model.QCategory>createList("categories", com.salesmanager.core.business.catalog.category.model.Category.class, com.salesmanager.core.business.catalog.category.model.QCategory.class, PathInits.DIRECT2);

    public final ListPath<com.salesmanager.core.business.reference.country.model.Country, com.salesmanager.core.business.reference.country.model.QCountry> countries = this.<com.salesmanager.core.business.reference.country.model.Country, com.salesmanager.core.business.reference.country.model.QCountry>createList("countries", com.salesmanager.core.business.reference.country.model.Country.class, com.salesmanager.core.business.reference.country.model.QCountry.class, PathInits.DIRECT2);

    public final ListPath<com.salesmanager.core.business.customer.model.Customer, com.salesmanager.core.business.customer.model.QCustomer> customers = this.<com.salesmanager.core.business.customer.model.Customer, com.salesmanager.core.business.customer.model.QCustomer>createList("customers", com.salesmanager.core.business.customer.model.Customer.class, com.salesmanager.core.business.customer.model.QCustomer.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final BooleanPath new$ = _super.new$;

    public final ListPath<com.salesmanager.core.business.catalog.product.model.Product, com.salesmanager.core.business.catalog.product.model.QProduct> products = this.<com.salesmanager.core.business.catalog.product.model.Product, com.salesmanager.core.business.catalog.product.model.QProduct>createList("products", com.salesmanager.core.business.catalog.product.model.Product.class, com.salesmanager.core.business.catalog.product.model.QProduct.class, PathInits.DIRECT2);

    public final NumberPath<Integer> promotionTragetAge = createNumber("promotionTragetAge", Integer.class);

    public final StringPath targetGender = createString("targetGender");

    public QPromotionRule(String variable) {
        super(PromotionRule.class, forVariable(variable));
    }

    public QPromotionRule(Path<? extends PromotionRule> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPromotionRule(PathMetadata<?> metadata) {
        super(PromotionRule.class, metadata);
    }

}

