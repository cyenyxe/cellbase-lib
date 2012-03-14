package org.bioinfo.infrared.lib.impl.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.bioinfo.infrared.core.cellbase.StructuralVariation;
import org.bioinfo.infrared.lib.api.StructuralVariationDBAdaptor;
import org.bioinfo.infrared.lib.common.Region;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

public class StructuralVariationHibernateDBAdaptor extends HibernateDBAdaptor implements StructuralVariationDBAdaptor{

	public StructuralVariationHibernateDBAdaptor(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public StructuralVariationHibernateDBAdaptor(SessionFactory sessionFactory, String species, String version) {
		super(sessionFactory, species, version);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StructuralVariation> getAllByRegion(Region region) {
		Query query = this.openSession().createQuery("select sv from StructuralVariation sv where sv.chromosome= :CHROMOSOME and sv.start < :END and sv.end > :START")
				.setParameter("CHROMOSOME", region.getChromosome())
				.setParameter("START", region.getStart())
				.setParameter("END", region.getEnd());
		return (List<StructuralVariation>) executeAndClose(query);
	}

	@Override
	public List<List<StructuralVariation>> getAllByRegionList(List<Region> regionList) {
		List<List<StructuralVariation>> result = new ArrayList<List<StructuralVariation>>(regionList.size());
		for (Region region : regionList) {
			result.add(this.getAllByRegion(region));
		}
		return result;
	}

}