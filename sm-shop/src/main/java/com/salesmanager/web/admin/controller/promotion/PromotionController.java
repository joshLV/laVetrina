package com.salesmanager.web.admin.controller.promotion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.salesmanager.core.business.catalog.category.model.Category;
import com.salesmanager.core.business.catalog.category.model.CategoryDescription;
import com.salesmanager.core.business.catalog.category.service.CategoryService;
import com.salesmanager.core.business.catalog.product.model.Product;
import com.salesmanager.core.business.catalog.product.model.description.ProductDescription;
import com.salesmanager.core.business.catalog.product.model.manufacturer.Manufacturer;
import com.salesmanager.core.business.catalog.product.model.relationship.ProductRelationship;
import com.salesmanager.core.business.catalog.product.model.relationship.ProductRelationshipType;
import com.salesmanager.core.business.catalog.product.service.ProductService;
import com.salesmanager.core.business.catalog.product.service.manufacturer.ManufacturerService;
import com.salesmanager.core.business.catalog.product.service.relationship.ProductRelationshipService;
import com.salesmanager.core.business.customer.model.Customer;
import com.salesmanager.core.business.customer.service.CustomerService;
import com.salesmanager.core.business.generic.exception.ServiceException;
import com.salesmanager.core.business.merchant.model.MerchantStore;
import com.salesmanager.core.business.promo.model.BounsType;
import com.salesmanager.core.business.promo.model.BundlePromotion;
import com.salesmanager.core.business.promo.model.CartPromotion;
import com.salesmanager.core.business.promo.model.Promotion;
import com.salesmanager.core.business.promo.model.PromotionDescription;
import com.salesmanager.core.business.promo.model.PromotionRule;
import com.salesmanager.core.business.promo.model.PromotionTragetAge;
import com.salesmanager.core.business.promo.model.PromotionType;
import com.salesmanager.core.business.promo.model.UpSellingPromotion;
import com.salesmanager.core.business.promotion.service.BounsService;
import com.salesmanager.core.business.promotion.service.BundlePromotionService;
import com.salesmanager.core.business.promotion.service.CartPromotionService;
import com.salesmanager.core.business.promotion.service.ProductAgeRangeSerivce;
import com.salesmanager.core.business.promotion.service.PromotionService;
import com.salesmanager.core.business.promotion.service.PromotionTypeService;
import com.salesmanager.core.business.promotion.service.UpSellingPromotionService;
import com.salesmanager.core.business.reference.country.model.Country;
import com.salesmanager.core.business.reference.country.service.CountryService;
import com.salesmanager.core.business.reference.language.model.Language;
import com.salesmanager.core.utils.ajax.AjaxPageableResponse;
import com.salesmanager.core.utils.ajax.AjaxResponse;
import com.salesmanager.web.admin.entity.web.Menu;
import com.salesmanager.web.constants.Constants;
import com.salesmanager.web.shop.controller.sale.facade.SaleFacade;
import com.salesmanager.web.shop.controller.sale.model.ProductModel;
import com.salesmanager.web.utils.DateUtil;
import com.salesmanager.web.utils.LabelUtils;

@Controller
public class PromotionController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PromotionController.class);	
	
	
	
	@Autowired
	PromotionService promotionService;
	@Autowired
	CartPromotionService cartPromotionService;
	@Autowired
	BundlePromotionService bundlePromotionService;
	@Autowired
	UpSellingPromotionService upSellingPromotionService;
	@Autowired
	LabelUtils messages;
	@Autowired
	CountryService countryService;
	@Autowired
	CustomerService customerService;
	@Autowired
	ProductService productService;
	@Autowired
	ManufacturerService mnufacturerService;
	@Autowired
	CategoryService categoryService;
	@Autowired
	ProductAgeRangeSerivce productAgeRangeSerivce;
	@Autowired
	PromotionTypeService promotionTypeService;
	
	@Autowired
	BounsService bounsService;
	
	@Autowired
	ProductRelationshipService productRelationshipService;

	@Autowired
	private SaleFacade saleFacade;
	

	
	
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/promotion/editPromotion.html", method=RequestMethod.GET)
	public String displayPromotionEdit(@RequestParam("id") long promotionId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return displayPromotion(promotionId,model,request,response);

	}
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/promotion/conditionTab.html", method=RequestMethod.GET)
	public String conditionTab(@RequestParam("id") long promotionId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		setMenu(model,request);
		List<PromotionTragetAge> promotionTragetAges=productAgeRangeSerivce.list();
		model.addAttribute("promotionTragetAges", promotionTragetAges);
		Promotion promotion=promotionService.getById(promotionId);
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
	    List<Manufacturer> manufacturers=mnufacturerService.listByStore(store);
		
		if(promotion.getPromotionRule()!=null && promotion.getPromotionRule().getBrands()!=null ){
			
			promotion.getPromotionRule().setBrandsId(new String[promotion.getPromotionRule().getBrands().size()]);
			for (int j = 0; j < promotion.getPromotionRule().getBrands().size(); j++) {	
				
				promotion.getPromotionRule().getBrandsId()[j]=promotion.getPromotionRule().getBrands().get(j).getId()+"";
			}
		}
	   Language language = (Language)request.getAttribute("LANGUAGE");
	   List<Category> categories = categoryService.listByStore(store,language);
		
		model.addAttribute("categories", categories);
		model.addAttribute("promotion", promotion);
		model.addAttribute("manufacturers",manufacturers);
		return "conditionTab";

	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/promotion/cartPromotion.html", method=RequestMethod.GET)
	public String cartPromotion(@RequestParam("id") long promotionId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		setMenu(model,request);
		
		CartPromotion cartPromotion=  promotionService.getCartPromotionById(promotionId);
		if(cartPromotion==null){
			cartPromotion=new CartPromotion();
			cartPromotion.setPromotion(new Promotion());
			cartPromotion.getPromotion().setId(promotionId);
		}
		model.addAttribute("cartPromotion", cartPromotion);
		return "cartPromotion";

	}
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/promotion/bouns.html", method=RequestMethod.GET)
	public String displayPromotionBouns(@RequestParam("id") long promotionId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		setMenu(model, request);
		Promotion promotion=null;
		if( promotionId!=0) {
			promotion=promotionService.getById(promotionId);
		}
		model.addAttribute("promotion", promotion);
		List<BounsType> bounsTypes=bounsService.list();
		model.addAttribute("bounsList",bounsTypes);
		return "promotion-bouns";
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/promotion/addPromotion.html", method=RequestMethod.GET)
	public String displayPromotionAdd( Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return displayPromotion(0,model,request,response);

	}
	
	
	
	
	
	private String displayPromotion(long promotionId, Model model,
			HttpServletRequest request, HttpServletResponse response) throws Exception{
		setMenu(model,request);
		Promotion promotion=new Promotion();
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		
		if( promotionId!=0) {
			promotion=promotionService.getById(promotionId);
		}else{
			promotion.setPromotionDescriptions(new ArrayList<PromotionDescription>());
		}
		List<PromotionTragetAge> promotionTragetAges=productAgeRangeSerivce.list();
		List<PromotionType> promotionTypes=promotionTypeService.list();
		List<Language> languages = store.getLanguages();
		for(Language language:languages){
			PromotionDescription promotionDescription = null;
			for(PromotionDescription desc : promotion.getPromotionDescriptions()) {
				
				
				if(desc.getLanguageId()==language.getId()) {
					promotionDescription = desc;
					promotionDescription.setLanguageName(language.getCode());
				}
		}
			if(promotionDescription==null) {
				promotionDescription = new PromotionDescription();
				promotionDescription.setLanguageId(language.getId());
				promotionDescription.setLanguageName(language.getCode());
				promotion.getPromotionDescriptions().add(promotionDescription);
			}

			
			
		}
		List<Manufacturer> manufacturers=mnufacturerService.listByStore(store);
		
		if(promotion.getPromotionRule()!=null && promotion.getPromotionRule().getBrands()!=null ){
			
			promotion.getPromotionRule().setBrandsId(new String[promotion.getPromotionRule().getBrands().size()]);
			for (int j = 0; j < promotion.getPromotionRule().getBrands().size(); j++) {	
				
				promotion.getPromotionRule().getBrandsId()[j]=promotion.getPromotionRule().getBrands().get(j).getId()+"";
			}
		}
		
		model.addAttribute("languages",languages);
		model.addAttribute("promotion", promotion);
		model.addAttribute("promotionTragetAges", promotionTragetAges);
		model.addAttribute("promotionTypes", promotionTypes);
		model.addAttribute("manufacturers",manufacturers);
		
		return "promotion";
	}







	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/promotion/promotions.html", method=RequestMethod.GET)
	public String displayPromotions(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		setMenu(model,request);
		
		
		
		//does nothing, ajax subsequent request
		
		
		return "promotions";
	}
	@SuppressWarnings({ "unchecked"})
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/promotion/paging.html", method=RequestMethod.POST, produces={"application/json; charset=UTF-8"})
	public @ResponseBody String pagecomplaints(HttpServletRequest request, HttpServletResponse response) {
		String name = request.getParameter("name");
		String status = request.getParameter("status");
        
        String startDate =request.getParameter("startDate");
        String endDate=request.getParameter("endDate");


		AjaxResponse resp = new AjaxResponse();

		
		try {
			
			Language language = (Language)request.getAttribute("LANGUAGE");
				
		
			
			
			List<Promotion> promotions = null;
					
			if(!StringUtils.isBlank(name) || !StringUtils.isBlank(status) || !StringUtils.isBlank(startDate)|| !StringUtils.isBlank(endDate)) {
				
				
				promotions = promotionService.listPromotion(language, name,status,startDate,endDate);
				
			} else  {
				
				//categoryService.listByCodes(store, new ArrayList<String>(Arrays.asList(categoryCode)), language);
			
			promotions = promotionService.listPromotion(language);
			}
				
			
			
			for(Promotion promotion : promotions) {
				
				@SuppressWarnings("rawtypes")
				Map entry = new HashMap();
				entry.put("id", promotion.getId());
				
				entry.put("name", promotion.getPromotionDescriptions().get(0).getName());
				entry.put("startDate", DateUtil.formatDate(promotion.getStartDate()));
				entry.put("endDate", DateUtil.formatDate(promotion.getEndate()));
				entry.put("status", promotion.getStatus());
				resp.addDataEntry(entry);
				
				
			}
			
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);
			

		
		} catch (Exception e) {
			LOGGER.error("Error while paging complaints", e);
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}
		
		String returnString = resp.toJSONString();
		
		return returnString;
	}
	/**
	 * Configures the shipping mode, shows shipping countries
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/promotion/promotionCounties.html", method=RequestMethod.GET)
	public String displayShippingConfigs(@RequestParam("id") long promotionId,Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		setMenu(model, request);
		

		model.addAttribute("configuration", new Promotion());
		model.addAttribute("promotionId", promotionId);
		
		return "promotion-countries";
		
		
	}
	@SuppressWarnings({ "unchecked"})
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/promotion/countries/paging.html", method=RequestMethod.POST, produces={"application/json; charset=UTF-8"})
	public @ResponseBody String pageCountries(@RequestParam("id") long promotionId,HttpServletRequest request, HttpServletResponse response) {
		String countryName = request.getParameter("name");
		AjaxResponse resp = new AjaxResponse();

		try {
			
			Language language = (Language)request.getAttribute("LANGUAGE");
			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			Promotion promotion=null;
			if(request.getSession().getAttribute("promotion")==null){
				 promotion=promotionService.getById(promotionId);
			}else{
				promotion=(Promotion) request.getSession().getAttribute("promotion");
			}
			//get list of countries
			Map<String,Country> countries = countryService.getCountriesMap(language);
			
			//get inclusions
			List<Country> includedCountries =null;
			if(promotion.getPromotionRule()!=null){
			 includedCountries = promotion.getPromotionRule().getCountries();
			}
			

			for(String key : countries.keySet()) {
				
				Country country = (Country)countries.get(key);

				@SuppressWarnings("rawtypes")
				Map entry = new HashMap();
				entry.put("code", country.getIsoCode());
				entry.put("name", country.getName());
				
				if(includedCountries!=null &&includedCountries.contains(country)) {
					entry.put("supported", true);
				} else {
					entry.put("supported", false);
				}
				
				if(!StringUtils.isBlank(countryName)) {
					if(country.getName().contains(countryName)){
						resp.addDataEntry(entry);
					}
				} else {
					resp.addDataEntry(entry);
				}
			}
			request.getSession().setAttribute("promotion",promotion);
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);

		} catch (Exception e) {
			LOGGER.error("Error while paging shipping countries", e);
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}
		
		String returnString = resp.toJSONString();
		
		return returnString;
	}
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/promotion/countries/update.html", method=RequestMethod.POST, produces={"application/json; charset=UTF-8"})
	public @ResponseBody String updateCountry(HttpServletRequest request, HttpServletResponse response) {
		String values = request.getParameter("_oldValues");
		String supported = request.getParameter("supported");
		String id = request.getParameter("id");
		
		

		
		
		AjaxResponse resp = new AjaxResponse();

		try {
			
			ObjectMapper mapper = new ObjectMapper();
			@SuppressWarnings("rawtypes")
			Map conf = mapper.readValue(values, Map.class);
			
			String countryCode = (String)conf.get("code");

			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			Promotion promotion=(Promotion) request.getSession().getAttribute("promotion");
			//get list of countries
			if(promotion.getPromotionRule()==null){
				promotion.setPromotionRule(new PromotionRule());
				
			}
			
			List<Country> includedCountries = promotion.getPromotionRule().getCountries();
			if(includedCountries==null){
				includedCountries=new ArrayList<Country>();
				promotion.getPromotionRule().setCountries(includedCountries);
			}
			if(!StringUtils.isBlank(supported)) {
				if("true".equals(supported)) {
					promotion.getPromotionRule().getCountries().add(countryService.getByCode(countryCode));
				} else {
					promotion.getPromotionRule().getCountries().remove(countryCode);
				}
			}
			//promotion.getPromotionRule().setCountries(includedCountries);
			request.getSession().setAttribute("promotion",promotion);
			//promotionService.saveOrUpdate(promotion);
			
			resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
			

		
		} catch (Exception e) {
			LOGGER.error("Error while paging shipping countries", e);
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}
		
		String returnString = resp.toJSONString();
		
		return returnString;
	}
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/promotion/saveCountries.html", method=RequestMethod.GET)
	public String displayPromotionEdit( Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Promotion promotion=(Promotion) request.getSession().getAttribute("promotion");
		long promotionId=promotion.getId();
		promotionService.saveOrUpdate(promotion);
		request.getSession().removeAttribute("promotion");
		return "redirect:/admin/promotion/editPromotion.html?id="+promotionId;

	}
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/promotion/featured/list.html", method=RequestMethod.GET)
	public String displayFeaturedItems(@RequestParam("id")long promotionId,Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		setMenu(model, request);
		
		Language language = (Language)request.getAttribute("LANGUAGE");
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		

		List<ProductRelationship> relationships = productRelationshipService.getGroups(store);
		List<ProductRelationship> productRelationships=new ArrayList<ProductRelationship>();
		for(ProductRelationship relationship : relationships) {
			
			if(!"FEATURED_ITEM".equals(relationship.getCode())) {//do not add featured items

				productRelationships.add(relationship);
			
			}
			
		}
		BundlePromotion bundlePromotion=  promotionService.getBundlePromotionById(promotionId);
		if(bundlePromotion==null){
			bundlePromotion=new BundlePromotion();
			bundlePromotion.setPromotion(new Promotion());
			bundlePromotion.getPromotion().setId(promotionId);
		}
		model.addAttribute("bundlePromotion", bundlePromotion);
		model.addAttribute("relationships",productRelationships);
		model.addAttribute("promotionId", promotionId);
		return "admin-promotion-featured";
		
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/promotion/featured/paging.html", method=RequestMethod.POST, produces={"application/json; charset=UTF-8"})
	public @ResponseBody String pageProducts(@RequestParam("id")long id,HttpServletRequest request, HttpServletResponse response) {
		
		
		AjaxResponse resp = new AjaxResponse();
		
		try {
			

			
			Language language = (Language)request.getAttribute("LANGUAGE");
			
			

			Promotion promotion=null;
			if(request.getSession().getAttribute("promotion")==null){
				 promotion=promotionService.getById(id);
			}else{
				promotion=(Promotion) request.getSession().getAttribute("promotion");
			}
			
			
			for(Product product : promotion.getPromotionRule().getProducts()) {
				
				
				Map entry = new HashMap();
				
				entry.put("productId", product.getId());
				product = productService.getProductForLocale(product.getId(), language,new Locale(language.getCode()));
				ProductDescription description = product.getDescriptions().iterator().next();
				
				
				entry.put("name", description.getName());
				entry.put("sku", product.getSku());
				entry.put("available", product.isAvailable());
				resp.addDataEntry(entry);
				
			}
			request.getSession().setAttribute("promotion", promotion);

			resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		return returnString;


	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/promotion/featured/addItem.html", method=RequestMethod.POST, produces={"application/json; charset=UTF-8"})
	public @ResponseBody String addItem(HttpServletRequest request, HttpServletResponse response) {
		
		String productId = request.getParameter("productId");
		AjaxResponse resp = new AjaxResponse();
		
		try {
			

			Long lProductId = Long.parseLong(productId);

			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			Product product = productService.getById(lProductId);
			
			if(product==null) {
				resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				return resp.toJSONString();
			}
			
		


			Promotion promotion=(Promotion) request.getSession().getAttribute("promotion");
			
			List<Product> products=promotion.getPromotionRule().getProducts();
			if(products.contains(product)){
				resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				return resp.toJSONString();
			}
			else {
				
				promotion.getPromotionRule().getProducts().add(product);
			}
			
			request.getSession().setAttribute("promotion",promotion);
			resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		return returnString;
		
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/promotion/featured/removeItem.html&removeEntity=FEATURED", method=RequestMethod.POST, produces={"application/json; charset=UTF-8"})
	public @ResponseBody String removeItem(HttpServletRequest request, HttpServletResponse response) {
		
		String productId = request.getParameter("productId");
		AjaxResponse resp = new AjaxResponse();
		
		try {
			

			Long lproductId = Long.parseLong(productId);

			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			Product product = productService.getById(lproductId);
			
			if(product==null) {
				resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				return resp.toJSONString();
			}
			
			
			Promotion promotion=(Promotion)request.getSession().getAttribute("promotion");
			List<Product> products=promotion.getPromotionRule().getProducts();
			if(products.contains(product)){
				promotion.getPromotionRule().getProducts().remove(product);
			}
			else {
				resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				return resp.toJSONString();
			}
			
			


			
			
			request.getSession().setAttribute("promotion",promotion);

			resp.setStatus(AjaxPageableResponse.RESPONSE_OPERATION_COMPLETED);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging products", e);
			resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		return returnString;
		
	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/promotion/customers.html", method=RequestMethod.GET)
	public String displaypromotionCustomer(@RequestParam("id") long promotionId,Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		setMenu(model, request);

		
		model.addAttribute("promotionId", promotionId);
		
		return "promotion-customers";
		
		
	}
	@SuppressWarnings({ "unchecked"})
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/promotion/customers/paging.html", method=RequestMethod.POST, produces={"application/json; charset=UTF-8"})
	public @ResponseBody String pageCustomers(@RequestParam("id") long promotionId,HttpServletRequest request, HttpServletResponse response) {
		
		AjaxResponse resp = new AjaxResponse();
		

		try {
			
			
			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			Promotion promotion=null;
			if(request.getSession().getAttribute("promotion")==null){
				 promotion=promotionService.getById(promotionId);
			}else{
				promotion=(Promotion) request.getSession().getAttribute("promotion");
			}
			//get list of customers
			List<Customer> customers = customerService.listByStore(store);
			
			//get inclusions
			List<Customer> includedCountries = promotion.getPromotionRule().getCustomers();
			

			for(Customer customer : customers) {
				
				

				@SuppressWarnings("rawtypes")
				Map entry = new HashMap();
				entry.put("id", customer.getId());
				entry.put("firstName", customer.getBilling().getFirstName());
				entry.put("lastName", customer.getBilling().getLastName());
				entry.put("email", customer.getEmailAddress());
				entry.put("gender", customer.getGender()==null?"":customer.getGender().toString());
				entry.put("birthDate", DateUtil.formatDate(customer.getDateOfBirth()));
				entry.put("ageRange",DateUtil.getAgeRange(customer.getDateOfBirth()) );
				entry.put("interset","" );
				if(includedCountries!=null && includedCountries.contains(customer)) {
					entry.put("supported", true);
				} else {
					entry.put("supported", false);
				}
				
				
				resp.addDataEntry(entry);
				
			}
			request.getSession().setAttribute("promotion",promotion);
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);

		} catch (Exception e) {
			LOGGER.error("Error while paging shipping countries", e);
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}
		
		String returnString = resp.toJSONString();
		
		return returnString;
	}
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/promotion/customers/update.html", method=RequestMethod.POST, produces={"application/json; charset=UTF-8"})
	public @ResponseBody String updateCustomers(HttpServletRequest request, HttpServletResponse response) {
		String values = request.getParameter("_oldValues");
		String supported = request.getParameter("supported");
		String id = request.getParameter("id");
		
		

		
		
		AjaxResponse resp = new AjaxResponse();

		try {
			
			ObjectMapper mapper = new ObjectMapper();
			@SuppressWarnings("rawtypes")
			Map conf = mapper.readValue(values, Map.class);
			
			int customerId = (int)conf.get("id");

			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			Promotion promotion=(Promotion) request.getSession().getAttribute("promotion");
			if(promotion.getPromotionRule()==null){
				promotion.setPromotionRule(new PromotionRule());
			}
			if(promotion.getPromotionRule().getCustomers()==null){
				promotion.getPromotionRule().setCustomers(new ArrayList<Customer>());
			}
			
			if(!StringUtils.isBlank(supported)) {
				if("true".equals(supported)) {
					promotion.getPromotionRule().getCustomers().add(customerService.getById((long) customerId));
				} else {
					promotion.getPromotionRule().getCustomers().remove(customerId);
				}
			}
			//promotion.getPromotionRule().setCountries(includedCountries);
			request.getSession().setAttribute("promotion",promotion);
			//promotionService.saveOrUpdate(promotion);
			
			resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
			

		
		} catch (Exception e) {
			LOGGER.error("Error while paging shipping countries", e);
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}
		
		String returnString = resp.toJSONString();
		
		return returnString;
	}
	
	
private void setMenu(Model model, HttpServletRequest request) throws Exception {
		
		//display menu
		Map<String,String> activeMenus = new HashMap<String,String>();
		
		activeMenus.put("Offers", "Offers");
		activeMenus.put("create-offer", "create-offer");
		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>)request.getAttribute("MENUMAP");
		
		Menu currentMenu = (Menu)menus.get("Offers");
		model.addAttribute("currentMenu",currentMenu);
		model.addAttribute("activeMenus",activeMenus);
		//
		
	}


@PreAuthorize("hasRole('PRODUCTS')")
@RequestMapping(value="/admin/promotion/displayPromotionToCategories.html", method=RequestMethod.GET)
public String displayAddPromotionToCategories(@RequestParam("id") long promotionId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

	setMenu(model, request);
	model.addAttribute("promotionId", promotionId);
	return "promotion-categories";
	
}
/**
 * List all categories associated to a Product
 * @param request
 * @param response
 * @return
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@PreAuthorize("hasRole('PRODUCTS')")
@RequestMapping(value="/admin/promotion-categories/paging.html", method=RequestMethod.POST, produces={"application/json; charset=UTF-8"})
public @ResponseBody String pagePromotionCategories(@RequestParam("id") long promotionId,HttpServletRequest request, HttpServletResponse response) {

	
	MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
	
	
	AjaxResponse resp = new AjaxResponse();
	
	
	try {
		Language language = (Language)request.getAttribute("LANGUAGE");
		
		Promotion promotion=null;
		if(request.getSession().getAttribute("promotion")==null){
			 promotion=promotionService.getById(promotionId);
		}else{
			promotion=(Promotion) request.getSession().getAttribute("promotion");
		}
		//get list of countries
		List<Category> categories = categoryService.listByStore(store);
		
		//get inclusions
		List<Category> includedCountries = promotion.getPromotionRule().getCategories();
		

		for(Category category : categories) {
			
			Map entry = new HashMap();
			entry.put("code", category.getId());
			List<CategoryDescription> descriptions = category.getDescriptions();
			String categoryName = category.getDescriptions().get(0).getName();
			for(CategoryDescription description : descriptions){
				if(description.getLanguage().getCode().equals(language.getCode())) {
					categoryName = description.getName();
				}
			}
			entry.put("name", categoryName);
			if(includedCountries.contains(category)) {
				entry.put("supported", true);
			} else {
				entry.put("supported", false);
			}
			
			
			resp.addDataEntry(entry);
			
		}
		request.getSession().setAttribute("promotion",promotion);
		resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);

	
	} catch (Exception e) {
		LOGGER.error("Error while paging products", e);
		resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
		resp.setErrorMessage(e);
	}
	
	String returnString = resp.toJSONString();
	return returnString;


}


@PreAuthorize("hasRole('PRODUCTS')")
@RequestMapping(value="/admin/promotion/categories/update.html", method=RequestMethod.POST, produces={"application/json; charset=UTF-8"})
public @ResponseBody String updateCategories(HttpServletRequest request, HttpServletResponse response) {
	String values = request.getParameter("_oldValues");
	String supported = request.getParameter("supported");
	
	
	AjaxResponse resp = new AjaxResponse();

	try {
		
		ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("rawtypes")
		Map conf = mapper.readValue(values, Map.class);
		
		int countryCode = (int)conf.get("code");

		
		
		Promotion promotion=(Promotion) request.getSession().getAttribute("promotion");
		
		
		if(!StringUtils.isBlank(supported)) {
			if("true".equals(supported)) {
				promotion.getPromotionRule().getCategories().add(categoryService.getById((long) (countryCode)));
			} else {
				promotion.getPromotionRule().getCategories().remove((countryCode));
			}
		}
		//promotion.getPromotionRule().setCountries(includedCountries);
		request.getSession().setAttribute("promotion",promotion);
		//promotionService.saveOrUpdate(promotion);
		
		resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
		

	
	} catch (Exception e) {
		LOGGER.error("Error while paging shipping countries", e);
		resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
	}
	
	String returnString = resp.toJSONString();
	
	return returnString;
}

@PreAuthorize("hasRole('PRODUCTS')")
@RequestMapping(value="/admin/promotion/save.html", method=RequestMethod.POST)
public String saveNotification(@Valid @ModelAttribute("promotion") Promotion promotion, BindingResult result, Model model, HttpServletRequest request, Locale locale) throws Exception {

	setMenu(model, request);
	
	
	MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
	
	
	
	List<PromotionTragetAge> promotionTragetAges=productAgeRangeSerivce.list();
	List<PromotionType> promotionTypes=promotionTypeService.list();
	List<Language> languages = store.getLanguages();
	if(promotion.getStartDate()==null){
		ObjectError error = new ObjectError("promotion.startDate","Start Date is required");
		result.addError(error);
	}
	if(promotion.getEndate()==null){
		ObjectError error = new ObjectError("promotion.endate","End Date is required");
		result.addError(error);
	}if("-1".equals(promotion.getStatus())){
		ObjectError error = new ObjectError("promotion.status","Status is required");
		result.addError(error);
	}	
	int i=0;
	for(Language language:languages){
		
		PromotionDescription promotionDescription = null;
		if(promotion.getPromotionDescriptions()!=null){
		for(PromotionDescription desc : promotion.getPromotionDescriptions()) {
			
			
			if(desc.getLanguageId()==language.getId()) {
				if(desc.getName()==null || "".equals(desc.getName())){
					ObjectError error = new ObjectError("promotionDescriptions["+i+"].name",messages.getMessage("NotEmpty.promotion.promotionDescriptions[0].name", locale));
					result.addError(error);
				}
				promotionDescription = desc;
				promotionDescription.setLanguageName(language.getCode());
			}
	}
		if(promotionDescription==null) {
			
			
			
			promotionDescription = new PromotionDescription();
			promotionDescription.setLanguageId(language.getId());
			promotionDescription.setLanguageName(language.getCode());
			promotion.getPromotionDescriptions().add(promotionDescription);
		}

		i++;
		
	}
	}
	List<Manufacturer> manufacturers=mnufacturerService.listByStore(store);
	
	model.addAttribute("languages",languages);
	model.addAttribute("promotion", promotion);
	model.addAttribute("promotionTragetAges", promotionTragetAges);
	model.addAttribute("promotionTypes", promotionTypes);
	model.addAttribute("manufacturers",manufacturers);
	
	if (result.hasErrors()) {
		return "promotion";
	}
	if(promotion.getId()!=null && promotion.getId()>0){
		Promotion currPromotion=promotionService.getById(promotion.getId());

	if(currPromotion!=null){
		PromotionRule promotionRule=promotion.getPromotionRule();
		promotion.setPromotionRule(currPromotion.getPromotionRule());
		if(promotionRule!=null){
			promotion.getPromotionRule().setTargetGender(promotionRule.getTargetGender());
			promotion.getPromotionRule().setPromotionTragetAge(promotionRule.getPromotionTragetAge());
		}
	}else{
		
	}
	}
	if(promotion.getPromotionRule()!=null && promotion.getPromotionRule().getBrandsId()!=null ){
		promotion.getPromotionRule().setBrands(new ArrayList<Manufacturer>());
		for (int j = 0; j < promotion.getPromotionRule().getBrandsId().length; j++) {	
			Manufacturer manufacturer=new Manufacturer();
			manufacturer.setId(Long.parseLong(promotion.getPromotionRule().getBrandsId()[j]));
			promotion.getPromotionRule().getBrands().add(manufacturers.get(manufacturers.indexOf(manufacturer)));
		}
	}
	promotionService.saveOrUpdate(promotion);
	model.addAttribute("success","success");
	return "promotion";
}


@PreAuthorize("hasRole('PRODUCTS')")
@RequestMapping(value="/admin/promotion/saveConditionTab.html", method=RequestMethod.POST)
public String saveConditionTab(@Valid @ModelAttribute("promotion") Promotion promotion, BindingResult result, Model model, HttpServletRequest request, Locale locale) throws Exception {

	setMenu(model, request);
	
	
	MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
	
	
	
	List<PromotionTragetAge> promotionTragetAges=productAgeRangeSerivce.list();
	List<PromotionType> promotionTypes=promotionTypeService.list();
	List<Language> languages = store.getLanguages();
	
	List<Manufacturer> manufacturers=mnufacturerService.listByStore(store);
	
	model.addAttribute("languages",languages);
	model.addAttribute("promotion", promotion);
	model.addAttribute("promotionTragetAges", promotionTragetAges);
	model.addAttribute("promotionTypes", promotionTypes);
	model.addAttribute("manufacturers",manufacturers);
	
	
	if(promotion.getId()!=null && promotion.getId()>0){
		Promotion currPromotion=promotionService.getById(promotion.getId());

	if(currPromotion!=null){
		PromotionRule promotionRule=promotion.getPromotionRule();
		promotion.setPromotionRule(currPromotion.getPromotionRule());
		if(promotion.getPromotionRule()==null){
			promotion.setPromotionRule(new PromotionRule());
		}
		promotion.setStatus(currPromotion.getStatus());
		promotion.setStartDate(currPromotion.getStartDate());
		promotion.setEndate(currPromotion.getEndate());
		promotion.setId(currPromotion.getId());
		promotion.setPromotionType(currPromotion.getPromotionType());
		promotion.setPromotionDescriptions(currPromotion.getPromotionDescriptions());
		if(promotionRule!=null ){
			promotion.getPromotionRule().setTargetGender(promotionRule.getTargetGender());
			promotion.getPromotionRule().setPromotionTragetAge(promotionRule.getPromotionTragetAge());
		}
	}else{
		
	}
	}
	if(promotion.getPromotionRule()!=null && promotion.getPromotionRule().getBrandsId()!=null ){
		promotion.getPromotionRule().setBrands(new ArrayList<Manufacturer>());
		for (int j = 0; j < promotion.getPromotionRule().getBrandsId().length; j++) {	
			Manufacturer manufacturer=new Manufacturer();
			manufacturer.setId(Long.parseLong(promotion.getPromotionRule().getBrandsId()[j]));
			promotion.getPromotionRule().getBrands().add(manufacturers.get(manufacturers.indexOf(manufacturer)));
		}
	}
	 Language language = (Language)request.getAttribute("LANGUAGE");
	List<Category> categories = categoryService.listByStore(store,language);
	
	model.addAttribute("categories", categories);
	if(promotion.getPromotionRule()!=null && promotion.getPromotionRule().getCategoriesId()!=null ){
		promotion.getPromotionRule().setCategories(new ArrayList<Category>());
		for (int j = 0; j < promotion.getPromotionRule().getCategoriesId().length; j++) {	
			Category category=new Category();
			category.setId(Long.parseLong(promotion.getPromotionRule().getCategoriesId()[j]));
			promotion.getPromotionRule().getCategories().add(categories.get(manufacturers.indexOf(category)));
		}
	}
	promotionService.saveOrUpdate(promotion);
	model.addAttribute("success","success");
	return "conditionTab";
}

@PreAuthorize("hasRole('PRODUCTS')")
@RequestMapping(value="/admin/promotion/saveCartPromotion.html", method=RequestMethod.POST)
public String saveCartPromotion(@Valid @ModelAttribute("cartPromotion") CartPromotion cartPromotion, BindingResult result, Model model, HttpServletRequest request, Locale locale) throws Exception {

	setMenu(model, request);
	
	MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
	if(cartPromotion.getCouponCode()==null || "".equalsIgnoreCase(cartPromotion.getCouponCode())){
		ObjectError error = new ObjectError("couponCode",messages.getMessage("NotEmpty.promotion.couponCode", locale));
		result.addError(error);
	}else if(!isAlphaNumeric(cartPromotion.getCouponCode())){
		ObjectError error = new ObjectError("couponCode",messages.getMessage("Alpha.promotion.couponCode", locale));
		result.addError(error);
	}
	if(cartPromotion.getCouponDiscountType()==-1){
		ObjectError error = new ObjectError("couponDiscountType",messages.getMessage("NotEmpty.promotion.couponDiscountType", locale));
		result.addError(error);
	}
	if(cartPromotion.getCouponDiscountAmount()==0 || cartPromotion.getCouponDiscountAmount()==0.0){
		ObjectError error = new ObjectError("couponDiscountAmount",messages.getMessage("NotEmpty.promotion.couponDiscountAmount", locale));
		result.addError(error);
	}
	if (result.hasErrors()) {
		return "cartPromotion";
	}
	cartPromotion.setPromotion(promotionService.getById(cartPromotion.getPromotion().getId()));
	
	cartPromotionService.saveOrUpdate(cartPromotion);
	
	model.addAttribute("success","success");
	return "cartPromotion";
}


@PreAuthorize("hasRole('PRODUCTS')")
@RequestMapping(value="/admin/promotion/saveCrossSellingPromotion.html", method=RequestMethod.POST)
public String saveCrossSellingPromotion(@Valid @ModelAttribute("bundlePromotion") BundlePromotion bundlePromotion, BindingResult result, Model model, HttpServletRequest request, Locale locale) throws Exception {

	setMenu(model, request);
	
	MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
	
	
	if(bundlePromotion.getBundlePrice()==0 || bundlePromotion.getBundlePrice()==0.0){
		ObjectError error = new ObjectError("bundlePrice",messages.getMessage("NotEmpty.promotion.bundlePrice", locale));
		result.addError(error);
	}
	if (result.hasErrors()) {
		return "admin-promotion-featured";
	}
	bundlePromotion.setPromotion(promotionService.getById(bundlePromotion.getPromotion().getId()));
	
	bundlePromotionService.saveOrUpdate(bundlePromotion);
	List<ProductRelationship> relationships = productRelationshipService.getGroups(store);
	List<ProductRelationship> productRelationships=new ArrayList<ProductRelationship>();
	for(ProductRelationship relationship : relationships) {
		
		if(!"FEATURED_ITEM".equals(relationship.getCode())) {//do not add featured items

			productRelationships.add(relationship);
		
		}
		
	}
	model.addAttribute("relationships","productRelationships");
	model.addAttribute("success","success");
	return "admin-promotion-featured";
}



@PreAuthorize("hasRole('PRODUCTS')")
@RequestMapping(value="/admin/promotion/saveupSellingPromotion.html", method=RequestMethod.POST)
public String saveupSellingPromotion(@Valid @ModelAttribute("upSellingPromotion") UpSellingPromotion upSellingPromotion, BindingResult result, Model model, HttpServletRequest request, Locale locale) throws Exception {

	setMenu(model, request);
	
	MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
	
	
	if(upSellingPromotion.getValue()==0 || upSellingPromotion.getValue()==0.0){
		ObjectError error = new ObjectError("value",messages.getMessage("NotEmpty.promotion.value", locale));
		result.addError(error);
	}
	if (result.hasErrors()) {
		return "upsellingPromotion";
	}
	upSellingPromotion.setPromotion(promotionService.getById(upSellingPromotion.getPromotion().getId()));
	Language language = (Language)request.getAttribute("LANGUAGE");
	upSellingPromotionService.saveOrUpdate(upSellingPromotion);
	List<Category> categories = saleFacade.getAllCategories(store,language);

	request.getSession().setAttribute("categories", categories);

   model.addAttribute("categories", categories);
	model.addAttribute("success","success");
	return "upsellingPromotion";
}
@PreAuthorize("hasRole('PRODUCTS')")
@RequestMapping(value="/admin/promotion/displayUpSellingItems.html", method=RequestMethod.GET)
public String displayUpSellingItems(@RequestParam("id")long promotionId,Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
	setMenu(model, request);
	
	Language language = (Language)request.getAttribute("LANGUAGE");
	MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
	

	List<Category> categories = saleFacade.getAllCategories(store,language);

	request.getSession().setAttribute("categories", categories);

   model.addAttribute("categories", categories);
	UpSellingPromotion upSellingPromotion=  promotionService.getUpSellingPromotionById(promotionId);
	if(upSellingPromotion==null){
		upSellingPromotion=new UpSellingPromotion();
		upSellingPromotion.setPromotion(new Promotion());
		upSellingPromotion.getPromotion().setId(promotionId);
	}
	model.addAttribute("upSellingPromotion", upSellingPromotion);
	
	return "upsellingPromotion";
	
}


@RequestMapping(value = "/admin/promotion/loadProducts", method = RequestMethod.GET, produces = "application/json")
public @ResponseBody List<ProductModel> loadProducts(HttpServletRequest request,
		@RequestParam("categoryId") String categoryId) throws ServiceException {

	Language language = (Language)request.getAttribute("LANGUAGE");

	List<Product> products = new ArrayList<Product>();
	List<Long> categoryIds = new ArrayList<Long>();
	if (categoryId != null && !categoryId.isEmpty()) {
		long category = Long.parseLong(categoryId);
		categoryIds.add(category);
		products = saleFacade.loadProducts(categoryIds, language);
	}

	List<ProductModel> pm = new ArrayList<ProductModel>();
	for(Product p:products){

		ProductModel m = new ProductModel();
		m.setId(p.getId());
		m.setName( ((ProductDescription) p.getDescriptions().toArray()[0]).getName());

		pm.add(m);
	}

	request.setAttribute("products", pm);
	return pm;
}
private boolean isAlphaNumeric(String s){
    String pattern= "^[a-zA-Z0-9]*$";
        if(s.matches(pattern)){
            return true;
        }
        return false;   
}
@InitBinder     
protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
	binder.registerCustomEditor(List.class, "brands", new CustomCollectionEditor(List.class));
}
}
