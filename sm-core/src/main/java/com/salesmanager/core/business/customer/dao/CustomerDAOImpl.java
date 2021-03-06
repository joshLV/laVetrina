package com.salesmanager.core.business.customer.dao;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import com.salesmanager.core.business.customer.model.Customer;
import com.salesmanager.core.business.customer.model.CustomerCriteria;
import com.salesmanager.core.business.customer.model.CustomerGender;
import com.salesmanager.core.business.customer.model.CustomerList;
import com.salesmanager.core.business.customer.model.CustomerShareLog;
import com.salesmanager.core.business.customer.model.QCustomer;
import com.salesmanager.core.business.customer.model.QCustomerShareLog;
import com.salesmanager.core.business.customer.model.attribute.QCustomerAttribute;
import com.salesmanager.core.business.customer.model.attribute.QCustomerOption;
import com.salesmanager.core.business.customer.model.attribute.QCustomerOptionValue;
import com.salesmanager.core.business.generic.dao.SalesManagerEntityDaoImpl;
import com.salesmanager.core.business.merchant.model.MerchantStore;
import com.salesmanager.core.business.merchant.model.QMerchantStore;
import com.salesmanager.core.business.reference.country.model.QCountry;
import com.salesmanager.core.business.reference.zone.model.QZone;

@Repository("customerDao")
public class CustomerDAOImpl extends SalesManagerEntityDaoImpl<Long, Customer> implements CustomerDAO {

	public CustomerDAOImpl() {
		super();
	}
	
	
	@Override
	public Customer getById(Long id){
		QCustomer qCustomer = QCustomer.customer;
		QCountry qCountry = QCountry.country;
		QZone qZone = QZone.zone;
		QCustomerAttribute qCustomerAttribute = QCustomerAttribute.customerAttribute;
		QCustomerOption qCustomerOption = QCustomerOption.customerOption;
		QCustomerOptionValue qCustomerOptionValue = QCustomerOptionValue.customerOptionValue;
		
		JPQLQuery query = new JPAQuery (getEntityManager());
		
		query.from(qCustomer)
			.join(qCustomer.merchantStore).fetch()
			//.leftJoin(qCustomer.billing.country,qCountry).fetch()
			//.leftJoin(qCustomer.billing.zone,qZone).fetch()
			.leftJoin(qCustomer.defaultLanguage).fetch()
			.leftJoin(qCustomer.attributes,qCustomerAttribute).fetch()
			.leftJoin(qCustomerAttribute.customerOption, qCustomerOption).fetch()
			.leftJoin(qCustomerAttribute.customerOptionValue, qCustomerOptionValue).fetch()
			.leftJoin(qCustomerOption.descriptions).fetch()
			.leftJoin(qCustomerOptionValue.descriptions).fetch()
			.where(qCustomer.id.eq(id));
		
		return query.uniqueResult(qCustomer);
	}
	

	
	@Override
	public List<Customer> getByName(String name){
		QCustomer qCustomer = QCustomer.customer;
		QCountry qCountry = QCountry.country;
		QZone qZone = QZone.zone;
		QCustomerAttribute qCustomerAttribute = QCustomerAttribute.customerAttribute;
		QCustomerOption qCustomerOption = QCustomerOption.customerOption;
		QCustomerOptionValue qCustomerOptionValue = QCustomerOptionValue.customerOptionValue;
		
		
		JPQLQuery query = new JPAQuery (getEntityManager());
		
		query.from(qCustomer)
			.join(qCustomer.merchantStore).fetch()
			//.leftJoin(qCustomer.billing.country,qCountry).fetch()
			//.leftJoin(qCustomer.billing.zone,qZone).fetch()
			.leftJoin(qCustomer.defaultLanguage).fetch()
			.leftJoin(qCustomer.attributes,qCustomerAttribute).fetch()
			.leftJoin(qCustomerAttribute.customerOption, qCustomerOption).fetch()
			.leftJoin(qCustomerAttribute.customerOptionValue, qCustomerOptionValue).fetch()
			.leftJoin(qCustomerOption.descriptions).fetch()
			.leftJoin(qCustomerOptionValue.descriptions).fetch()
			.where(
					qCustomer.billing.firstName.eq(name).or(qCustomer.billing.lastName.eq(name))
					
			);
		
		return query.list(qCustomer);
	}
	
	
	@Override
	public List<Customer> getBySearchCritera(String firstname,String lastname,String gender,String birthDate,String country,String email){
		QCustomer qCustomer = QCustomer.customer;
		QCountry qCountry = QCountry.country;
		QZone qZone = QZone.zone;
		QCustomerAttribute qCustomerAttribute = QCustomerAttribute.customerAttribute;
		QCustomerOption qCustomerOption = QCustomerOption.customerOption;
		QCustomerOptionValue qCustomerOptionValue = QCustomerOptionValue.customerOptionValue;
		CustomerGender customerGender=null;
		
		BooleanExpression predicate=null;
		if(!StringUtils.isBlank(firstname)){
			predicate=qCustomer.billing.firstName.like("%"+firstname+"%");
		}if(!StringUtils.isBlank(lastname)){
			if(predicate!=null)
			predicate.and(qCustomer.billing.lastName.like("%"+lastname+"%"));
			else
				predicate=	qCustomer.billing.lastName.like("%"+lastname+"%");
		}if(!StringUtils.isBlank(email)){
			if(predicate!=null)
			predicate.and(qCustomer.emailAddress.like("%"+email+"%"));
			else
				predicate=	qCustomer.emailAddress.like("%"+email+"%");
		}if(!StringUtils.isBlank(country)){
			if(predicate!=null)
		predicate.and(qCustomer.billing.country.isoCode.like(country));
			else
				predicate=	qCustomer.billing.country.isoCode.like(country);
		}if(!StringUtils.isBlank(birthDate)){
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date compDate=new Date();
			try {
				compDate = formatter.parse(birthDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(predicate!=null)
			predicate.and(qCustomer.dateOfBirth.between(compDate, new Date()));
			else
				predicate=	qCustomer.dateOfBirth.between(compDate, new Date());
		}
		if(gender!=null){
		 customerGender=CustomerGender.valueOf(gender);
		 if(predicate!=null)
				predicate.and(qCustomer.gender.eq(customerGender));
				else
					predicate=	qCustomer.gender.eq(customerGender);
		}
		JPQLQuery query = new JPAQuery (getEntityManager());
		
		query.from(qCustomer)
			.join(qCustomer.merchantStore).fetch()
			//.leftJoin(qCustomer.billing.country,qCountry).fetch()
			//.leftJoin(qCustomer.billing.zone,qZone).fetch()
			.leftJoin(qCustomer.defaultLanguage).fetch()
			.leftJoin(qCustomer.attributes,qCustomerAttribute).fetch()
			.leftJoin(qCustomerAttribute.customerOption, qCustomerOption).fetch()
			.leftJoin(qCustomerAttribute.customerOptionValue, qCustomerOptionValue).fetch()
			.leftJoin(qCustomerOption.descriptions).fetch()
			.leftJoin(qCustomerOptionValue.descriptions).fetch()
			.where(
					predicate
							
									
							
					
			);
		
		return query.list(qCustomer);
	}
	
	
	@Override
	public CustomerList listByStore(MerchantStore store, CustomerCriteria criteria) {
		
		
		
		CustomerList customerList = new CustomerList();
		StringBuilder countBuilderSelect = new StringBuilder();
		countBuilderSelect.append("select count(c) from Customer as c");
		
		StringBuilder countBuilderWhere = new StringBuilder();
		countBuilderWhere.append(" where c.merchantStore.id=:mId");

		if(!StringUtils.isBlank(criteria.getName())) {
			countBuilderWhere.append(" and c.billing.firstName like:nm");
			countBuilderWhere.append(" or c.billing.lastName like:nm");
		}
		
		if(!StringUtils.isBlank(criteria.getFirstName())) {
			countBuilderWhere.append(" and c..billing.firstName like:fn");
		}
		
		if(!StringUtils.isBlank(criteria.getLastName())) {
			countBuilderWhere.append(" and c.billing.lastName like:ln");
		}
		
		if(!StringUtils.isBlank(criteria.getEmail())) {
			countBuilderWhere.append(" and c.emailAddress like:email");
		}
		
		if(!StringUtils.isBlank(criteria.getCountry())) {
			countBuilderWhere.append(" and c.billing.country.isoCode like:country");
		}

		Query countQ = super.getEntityManager().createQuery(
				countBuilderSelect.toString() + countBuilderWhere.toString());

		countQ.setParameter("mId", store.getId());
		

		if(!StringUtils.isBlank(criteria.getName())) {
			countQ.setParameter("nm",new StringBuilder().append("%").append(criteria.getName()).append("%").toString());
		}
		
		if(!StringUtils.isBlank(criteria.getFirstName())) {
			countQ.setParameter("fn",new StringBuilder().append("%").append(criteria.getFirstName()).append("%").toString());
		}
		
		if(!StringUtils.isBlank(criteria.getLastName())) {
			countQ.setParameter("ln",new StringBuilder().append("%").append(criteria.getLastName()).append("%").toString());
		}
		
		if(!StringUtils.isBlank(criteria.getEmail())) {
			countQ.setParameter("email",new StringBuilder().append("%").append(criteria.getEmail()).append("%").toString());
		}
		
		if(!StringUtils.isBlank(criteria.getCountry())) {
			countQ.setParameter("country",new StringBuilder().append("%").append(criteria.getCountry()).append("%").toString());
		}
		


		Number count = (Number) countQ.getSingleResult ();

		customerList.setTotalCount(count.intValue());
		
        if(count.intValue()==0)
        	return customerList;
		
		
		
		QCustomer qCustomer = QCustomer.customer;
		QCountry qCountry = QCountry.country;
		QZone qZone = QZone.zone;
		QCustomerAttribute qCustomerAttribute = QCustomerAttribute.customerAttribute;
		QCustomerOption qCustomerOption = QCustomerOption.customerOption;
		QCustomerOptionValue qCustomerOptionValue = QCustomerOptionValue.customerOptionValue;
		
		
		JPQLQuery query = new JPAQuery (getEntityManager());
		
		query.from(qCustomer)
			.join(qCustomer.merchantStore).fetch()
			.leftJoin(qCustomer.defaultLanguage).fetch()
			.leftJoin(qCustomer.attributes,qCustomerAttribute).fetch()
			.leftJoin(qCustomerAttribute.customerOption, qCustomerOption).fetch()
			.leftJoin(qCustomerAttribute.customerOptionValue, qCustomerOptionValue).fetch()
			.leftJoin(qCustomerOption.descriptions).fetch()
			.leftJoin(qCustomerOptionValue.descriptions).fetch();

			query.where(qCustomer.merchantStore.id.eq(store.getId()));
			BooleanBuilder pBuilder = null;

		if(!StringUtils.isBlank(criteria.getName())) {
			if(pBuilder==null) {
				pBuilder = new BooleanBuilder();
			}
			pBuilder.and(qCustomer.billing.firstName.like(new StringBuilder().append("%").append(criteria.getName()).append("%").toString())
					.or(qCustomer.billing.lastName.like(new StringBuilder().append("%").append(criteria.getName()).append("%").toString())));

		}
		
		
		if(!StringUtils.isBlank(criteria.getFirstName())) {
			if(pBuilder==null) {
				pBuilder = new BooleanBuilder();
			}
			pBuilder.and(qCustomer.billing.firstName.like(new StringBuilder().append("%").append(criteria.getFirstName()).append("%").toString()));
		}
		
		if(!StringUtils.isBlank(criteria.getLastName())) {
			if(pBuilder==null) {
				pBuilder = new BooleanBuilder();
			}
			pBuilder.and(qCustomer.billing.lastName.like(new StringBuilder().append("%").append(criteria.getLastName()).append("%").toString()));
		}
		
		if(!StringUtils.isBlank(criteria.getEmail())) {
			if(pBuilder==null) {
				pBuilder = new BooleanBuilder();
			}
			pBuilder.and(qCustomer.emailAddress.like(new StringBuilder().append("%").append(criteria.getEmail()).append("%").toString()));
		}
		
		if(!StringUtils.isBlank(criteria.getCountry())) {
			if(pBuilder==null) {
				pBuilder = new BooleanBuilder();
			}
			pBuilder.and(qCustomer.billing.country.isoCode.like(new StringBuilder().append("%").append(criteria.getCountry()).append("%").toString()));
		}
		
		if(pBuilder!=null) {
			query.where(pBuilder);
		}
		

		
		if(criteria.getMaxCount()>0) {
			query.limit(criteria.getMaxCount());
			query.offset(criteria.getStartIndex());
		}
		
		
		customerList.setCustomers(query.list(qCustomer));

		return customerList;
		
	}
	

	@Override
	public Customer getByNick(String nick){
		QCustomer qCustomer = QCustomer.customer;
		QCountry qCountry = QCountry.country;
		QZone qZone = QZone.zone;
		QCustomerAttribute qCustomerAttribute = QCustomerAttribute.customerAttribute;
		QCustomerOption qCustomerOption = QCustomerOption.customerOption;
		QCustomerOptionValue qCustomerOptionValue = QCustomerOptionValue.customerOptionValue;
		

		
		JPQLQuery query = new JPAQuery (getEntityManager());
		
		query.from(qCustomer)
			.join(qCustomer.merchantStore).fetch()
			//.leftJoin(qCustomer.billing.country,qCountry).fetch()
			//.leftJoin(qCustomer.billing.zone,qZone).fetch()
			.leftJoin(qCustomer.defaultLanguage).fetch()
			.leftJoin(qCustomer.groups).fetch()
			.leftJoin(qCustomer.attributes,qCustomerAttribute).fetch()
			.leftJoin(qCustomerAttribute.customerOption, qCustomerOption).fetch()
			.leftJoin(qCustomerAttribute.customerOptionValue, qCustomerOptionValue).fetch()
			.leftJoin(qCustomerOption.descriptions).fetch()
			.leftJoin(qCustomerOptionValue.descriptions).fetch()
			.where(qCustomer.nick.eq(nick));
		
		return query.uniqueResult(qCustomer);
	}
	
	@Override
	public Customer getByEmail(String email){
		QCustomer qCustomer = QCustomer.customer;
		QCountry qCountry = QCountry.country;
		QZone qZone = QZone.zone;
		QCustomerAttribute qCustomerAttribute = QCustomerAttribute.customerAttribute;
		QCustomerOption qCustomerOption = QCustomerOption.customerOption;
		QCustomerOptionValue qCustomerOptionValue = QCustomerOptionValue.customerOptionValue;
		

		
		JPQLQuery query = new JPAQuery (getEntityManager());
		
		query.from(qCustomer)
			.join(qCustomer.merchantStore).fetch()
			//.leftJoin(qCustomer.billing.country,qCountry).fetch()
			//.leftJoin(qCustomer.billing.zone,qZone).fetch()
			.leftJoin(qCustomer.defaultLanguage).fetch()
			.leftJoin(qCustomer.groups).fetch()
			.leftJoin(qCustomer.attributes,qCustomerAttribute).fetch()
			.leftJoin(qCustomerAttribute.customerOption, qCustomerOption).fetch()
			.leftJoin(qCustomerAttribute.customerOptionValue, qCustomerOptionValue).fetch()
			.leftJoin(qCustomerOption.descriptions).fetch()
			.leftJoin(qCustomerOptionValue.descriptions).fetch()
			.where(qCustomer.emailAddress.eq(email));
		
		return query.uniqueResult(qCustomer);
	}
	
	@Override
	public Customer getByNick(String nick, int storeId){
		QCustomer qCustomer = QCustomer.customer;
		QMerchantStore qMerchantStore = QMerchantStore.merchantStore;
		QCountry qCountry = QCountry.country;
		QZone qZone = QZone.zone;
		QCustomerAttribute qCustomerAttribute = QCustomerAttribute.customerAttribute;
		QCustomerOption qCustomerOption = QCustomerOption.customerOption;
		QCustomerOptionValue qCustomerOptionValue = QCustomerOptionValue.customerOptionValue;
		
		try {
		
		JPQLQuery query = new JPAQuery (getEntityManager());
		
		query.from(qCustomer)
			.join(qCustomer.merchantStore, qMerchantStore).fetch()
			//.leftJoin(qCustomer.billing.country,qCountry).fetch()
			//.leftJoin(qCustomer.billing.zone,qZone).fetch()
			.leftJoin(qCustomer.defaultLanguage).fetch()
			.leftJoin(qCustomer.groups).fetch()
			.leftJoin(qCustomer.attributes,qCustomerAttribute).fetch()
			.leftJoin(qCustomerAttribute.customerOption, qCustomerOption).fetch()
			.leftJoin(qCustomerAttribute.customerOptionValue, qCustomerOptionValue).fetch()
			.leftJoin(qCustomerOption.descriptions).fetch()
			.leftJoin(qCustomerOptionValue.descriptions).fetch()
			.where(qCustomer.nick.eq(nick).and(qMerchantStore.id.eq(storeId)));
		
		return query.uniqueResult(qCustomer);
		
		} catch(NoResultException nre) {
			return null;
		}
	}
	

	@Override
	public Customer getByEmail(String email, Integer id) {
		QCustomer qCustomer = QCustomer.customer;
		QMerchantStore qMerchantStore = QMerchantStore.merchantStore;
		QCountry qCountry = QCountry.country;
		QZone qZone = QZone.zone;
		QCustomerAttribute qCustomerAttribute = QCustomerAttribute.customerAttribute;
		QCustomerOption qCustomerOption = QCustomerOption.customerOption;
		QCustomerOptionValue qCustomerOptionValue = QCustomerOptionValue.customerOptionValue;
		
		try {
		
		JPQLQuery query = new JPAQuery (getEntityManager());
		
		query.from(qCustomer)
			.join(qCustomer.merchantStore, qMerchantStore).fetch()
			//.leftJoin(qCustomer.billing.country,qCountry).fetch()
			//.leftJoin(qCustomer.billing.zone,qZone).fetch()
			.leftJoin(qCustomer.defaultLanguage).fetch()
			.leftJoin(qCustomer.groups).fetch()
			.leftJoin(qCustomer.attributes,qCustomerAttribute).fetch()
			.leftJoin(qCustomerAttribute.customerOption, qCustomerOption).fetch()
			.leftJoin(qCustomerAttribute.customerOptionValue, qCustomerOptionValue).fetch()
			.leftJoin(qCustomerOption.descriptions).fetch()
			.leftJoin(qCustomerOptionValue.descriptions).fetch()
			.where(qCustomer.emailAddress.eq(email).and(qMerchantStore.id.eq(id)));
		
		return query.uniqueResult(qCustomer);
		
		} catch(NoResultException nre) {
			return null;
		}
	}
	
	@Override
	public List<Customer> listByStore(MerchantStore store){
		QCustomer qCustomer = QCustomer.customer;
		QCountry qCountry = QCountry.country;
		QZone qZone = QZone.zone;
		QCustomerAttribute qCustomerAttribute = QCustomerAttribute.customerAttribute;
		QCustomerOption qCustomerOption = QCustomerOption.customerOption;
		QCustomerOptionValue qCustomerOptionValue = QCustomerOptionValue.customerOptionValue;
		
		
		JPQLQuery query = new JPAQuery (getEntityManager());
		
		query.from(qCustomer)
			.join(qCustomer.merchantStore).fetch()
			//.leftJoin(qCustomer.billing.country,qCountry).fetch()
			//.leftJoin(qCustomer.billing.zone,qZone).fetch()
			.leftJoin(qCustomer.defaultLanguage).fetch()
			.leftJoin(qCustomer.attributes,qCustomerAttribute).fetch()
			.leftJoin(qCustomerAttribute.customerOption, qCustomerOption).fetch()
			.leftJoin(qCustomerAttribute.customerOptionValue, qCustomerOptionValue).fetch()
			.leftJoin(qCustomerOption.descriptions).fetch()
			.leftJoin(qCustomerOptionValue.descriptions).fetch()
			.where(qCustomer.merchantStore.id.eq(store.getId()));
		
		return query.distinct().list(qCustomer);
	}

/**
 * Insert record in CUSTOMER_SHARE_LOG table 
 * @param customer
 */
	public void updateCustomerShare(Customer customer){
		EntityManager entitymanager= getEntityManager();
		QCustomerShareLog qCustomerShareLog= QCustomerShareLog.customerShareLog; 
		JPQLQuery query = new JPAQuery (entitymanager);
		query.from(qCustomerShareLog).where(qCustomerShareLog.customerId.eq(customer.getId()).and(qCustomerShareLog.transactionDate.before(new Date())));
		List<CustomerShareLog> result=query.list(qCustomerShareLog);
		// Check previous customer share
		if(result.size()==0){
			CustomerShareLog log = new CustomerShareLog();
			log.setCustomerId(customer.getId());
			log.setTransactionDate(new Date());
			entitymanager.persist(log);
		}
	
}

}
