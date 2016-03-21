package com.salesmanager.core.business.reference.country.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import com.salesmanager.core.business.generic.model.SalesManagerEntity;
import com.salesmanager.core.business.reference.geozone.model.GeoZone;
import com.salesmanager.core.business.reference.zone.model.Zone;
import com.salesmanager.core.constants.SchemaConstant;

@Entity
@Table(name = "COUNTRY", schema="lavetrina")
@Cacheable
public class Country extends SalesManagerEntity<Integer, Country> {
	private static final long serialVersionUID = -7388011537255588035L;

	@Id
	@Column(name="COUNTRY_ID")
	@TableGenerator(name = "TABLE_GEN", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT",
	pkColumnValue = "COUNTRY_SEQ_NEXT_VAL")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
	private Integer id;
	
	@OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
	private List<CountryDescription> descriptions = new ArrayList<CountryDescription>();
	@OneToMany(mappedBy = "city", cascade = CascadeType.ALL)
	private List<City> citys = new ArrayList<City>();
	@OneToMany(mappedBy = "country")
	private List<Zone> zones = new ArrayList<Zone>();
	
	@ManyToOne(targetEntity = GeoZone.class)
	@JoinColumn(name = "GEOZONE_ID")
	private GeoZone geoZone;
	
	public List<City> getCitys() {
		return citys;
	}

	public void setCitys(List<City> citys) {
		this.citys = citys;
	}

	@Column(name = "COUNTRY_SUPPORTED")
	private boolean supported = true;
	
	@Column(name = "COUNTRY_ISOCODE", unique=true, nullable = false)
	private String isoCode;
	
	@Transient
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Country() {
	}
	
	public Country(String isoCode) {
		this.setIsoCode(isoCode);
	}
	
	public boolean getSupported() {
		return supported;
	}

	public void setSupported(boolean supported) {
		this.supported = supported;
	}

	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}


	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public List<Zone> getZones() {
		return zones;
	}

	public void setZones(List<Zone> zones) {
		this.zones = zones;
	}

	public List<CountryDescription> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<CountryDescription> descriptions) {
		this.descriptions = descriptions;
	}

	public GeoZone getGeoZone() {
		return geoZone;
	}

	public void setGeoZone(GeoZone geoZone) {
		this.geoZone = geoZone;
	}

	
	

/*	public GeoZone getGeoZone() {
		return geoZone;
	}

	public void setGeoZone(GeoZone geoZone) {
		this.geoZone = geoZone;
	}*/
}
