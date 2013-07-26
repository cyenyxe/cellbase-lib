package org.bioinfo.cellbase.lib.impl.mongodb;

import org.bioinfo.cellbase.lib.api.ChromosomeDBAdaptor;
import org.bioinfo.cellbase.lib.api.ConservedRegionDBAdaptor;
import org.bioinfo.cellbase.lib.impl.DBAdaptorFactory;
import org.bioinfo.cellbase.lib.impl.dbquery.QueryOptions;
import org.bioinfo.commons.utils.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ChromosomeMongoDBAdaptorTest {

	private DBAdaptorFactory dbAdaptorFactory = new MongoDBAdaptorFactory();
    ConservedRegionDBAdaptor conservedRegionDBAdaptor;
	private String species = "hsapiens";
	private String version = "v3";

	@Before
	public void beforeTestStart() {
//        conservedRegionDBAdaptor = dbAdaptorFactory.getConservedRegionDBAdaptor(this.species, this.version);
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetByRegionList() {

        ChromosomeDBAdaptor dbAdaptor = dbAdaptorFactory.getChromosomeDBAdaptor(this.species, this.version);
//        System.out.println(dbAdaptor.getAll(new QueryOptions()));
        System.out.println(dbAdaptor.getAllByIdList(StringUtils.toList("20", ","), new QueryOptions()));

	}
	

	
}
