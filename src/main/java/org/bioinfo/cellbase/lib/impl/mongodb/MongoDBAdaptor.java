package org.bioinfo.cellbase.lib.impl.mongodb;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.bioinfo.cellbase.lib.common.IntervalFeatureFrequency;
import org.bioinfo.cellbase.lib.common.Region;
import org.bioinfo.cellbase.lib.impl.DBAdaptor;
import org.bioinfo.cellbase.lib.impl.dbquery.QueryOptions;
import org.bioinfo.cellbase.lib.impl.dbquery.QueryResponse;
import org.bioinfo.cellbase.lib.impl.dbquery.QueryResult;
import org.bioinfo.commons.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoDBAdaptor extends DBAdaptor {

	protected String species;
	protected String version;

	//	private MongoOptions mongoOptions;
	//	protected MongoClient mongoClient;
	protected DB db;
	protected DBCollection mongoDBCollection;
	protected static Map<String, Number> cachedQuerySizes = new HashMap<String, Number>();

	protected ObjectMapper jsonObjectMapper;

	static {
		// reading application.properties file
		resourceBundle = ResourceBundle.getBundle("org.bioinfo.cellbase.lib.impl.mongodb.conf.application");
		try {
			if (applicationProperties == null) {
				applicationProperties = new Config(resourceBundle);
			} else {
				// applicationProperties object must have been filled in DBAdpator class,
				// then just append MongoDB properties
				String key;
				Set<String> keys = resourceBundle.keySet();
				Iterator<String> keysIter = keys.iterator();
				while (keysIter.hasNext()) {
					key = keysIter.next();
					applicationProperties.put(key, resourceBundle.getObject(key));
				}
			}
		} catch (IOException e) {
			applicationProperties = new Config();
			e.printStackTrace();
		}
	}

	//	public MongoDBAdaptor(String species, String version) {
	//		logger.info("Species: "+species+" Version: "+version);
	//		this.mongoOptions = new MongoOptions();
	//		this.mongoOptions.setAutoConnectRetry(true);
	//		this.mongoOptions.setConnectionsPerHost(40);
	//		try {
	//			this.mongoClient = new MongoClient("mem15", mongoOptions);
	//		} catch (UnknownHostException e) {
	//			e.printStackTrace();
	//		}
	//	}

	public MongoDBAdaptor(DB db) {
		this.db = db;
	}

	public MongoDBAdaptor(DB db, String species, String version) {
		this.db = db;
		this.species = species;
		this.version = version;
		//		logger.warn(applicationProperties.toString());
		initSpeciesVersion(species, version);

		jsonObjectMapper = new ObjectMapper();
	}

	private void initSpeciesVersion(String species, String version) {
		if (species != null && !species.equals("")) {
			// if 'version' parameter has not been provided the default version is selected
			if (this.version == null || this.version.trim().equals("")) {
				this.version = applicationProperties.getProperty(species + ".DEFAULT.VERSION").toUpperCase();
				//				logger.debug("HibernateDBAdaptorFactory in createSessionFactory(): 'version' parameter is null or empty, it's been set to: '"+version+"'");
			}
		}
	}

	//	protected Session openSession() {
	//		if(session == null) {
	//			logger.debug("HibernateDBAdaptor: Session is null");
	//			session = sessionFactory.openSession();
	//		}else {
	//			if(!session.isOpen()) {
	//				logger.debug("HibernateDBAdaptor: Session is closed");
	//				session = sessionFactory.openSession();
	//			}else {
	//				logger.debug("HibernateDBAdaptor: Session is already open");
	//			}
	//		}
	//
	//		return session;
	//	}


	protected QueryOptions addExcludeReturnFields(String returnField, QueryOptions options) {
		if(options != null && !options.getBoolean(returnField, true)) {
			if(options.get("exclude") != null) {
				options.put("exclude", options.get("exclude")+","+returnField);
			}else {
				options.put("exclude", returnField);
			}
		}
		return options;
	}

	protected BasicDBObject getReturnFields(QueryOptions options) {
		// Select which fields are excluded and included in MongoDB query
		BasicDBObject returnFields = new BasicDBObject("_id", 0);
		// Read and process 'exclude' field from 'options' object
		if ((options != null && options.getList("include") != null) && options.getList("include").size() > 0) {
			List<Object> excludedOptionFields = (List<Object>) options.getList("include");
			if (excludedOptionFields != null && excludedOptionFields.size() > 0) {
				for (Object field : excludedOptionFields) {
					returnFields.put(field.toString(), 1);
				}
			}
		} else {
			if (options != null && options.getList("exclude") != null) {
				List<Object> excludedOptionFields = (List<Object>) options.getList("exclude");
				if (excludedOptionFields != null && excludedOptionFields.size() > 0) {
					for (Object field : excludedOptionFields) {
						returnFields.put(field.toString(), 0);
					}
				}
			}
		}
		return returnFields;
	}

    protected BasicDBList executeFind(DBObject query, DBObject returnFields) {
        return executeFind(query, returnFields, mongoDBCollection);
    }

    protected BasicDBList executeFind(DBObject query, DBObject returnFields, DBCollection dbCollection) {
        BasicDBList list = new BasicDBList();

        DBCursor cursor = dbCollection.find(query, returnFields);
        try {
            if (cursor != null) {
                while (cursor.hasNext()) {
                    list.add(cursor.next());
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return list;
    }


    protected QueryResponse executeQuery(Object id, DBObject query, QueryOptions options) {
        return executeQueryList(Arrays.asList(id), Arrays.asList(query), options, mongoDBCollection);
    }

    protected QueryResponse executeQueryList(List<? extends Object> ids, List<DBObject> queries, QueryOptions options) {
        return executeQueryList(ids, queries, options, mongoDBCollection);
    }

	protected QueryResponse executeQuery(Object id, DBObject query, QueryOptions options, DBCollection dbCollection) {
		return executeQueryList(Arrays.asList(id), Arrays.asList(query), options, dbCollection);
	}

	protected QueryResponse executeQueryList(List<? extends Object> ids, List<DBObject> queries, QueryOptions options, DBCollection dbCollection) {
		QueryResponse queryResponse = new QueryResponse();

		// Select which fields are excluded and included in MongoDB query
		BasicDBObject returnFields = getReturnFields(options);

		long timeStart = System.currentTimeMillis();
		long dbTimeStart, dbTimeEnd;
		for (int i = 0; i < queries.size(); i++) {
			DBObject query = queries.get(i);
			QueryResult queryResult = new QueryResult();

			// Execute query and calculate time
			dbTimeStart = System.currentTimeMillis();
			BasicDBList list = executeFind(query, returnFields, dbCollection);
			dbTimeEnd = System.currentTimeMillis();

			queryResult.setDBTime((dbTimeEnd - dbTimeStart));
			queryResult.setResult(list);

			// Save QueryResult into QueryResponse object
			queryResponse.put(ids.get(i).toString(), queryResult);
		}
		long timeEnd = System.currentTimeMillis();


		// Check if 'metadata' field must be returned
		if (options != null && options.getBoolean("metadata", true)) {
			queryResponse.getMetadata().put("queryIds", ids);
			queryResponse.getMetadata().put("time", timeEnd - timeStart);
		} else {
			queryResponse.removeField("metadata");
		}

		return queryResponse;
	}


    protected QueryResponse executeAggregation(Object id, DBObject[] operations, QueryOptions options) {
        return executeAggregation(id,operations,options,mongoDBCollection);
    }

    protected QueryResponse executeAggregationList(List<? extends Object> ids, List<DBObject[]> operationsList, QueryOptions options) {
        return executeAggregationList(ids,operationsList,options,mongoDBCollection);
    }

	protected QueryResponse executeAggregation(Object id, DBObject[] operations, QueryOptions options, DBCollection dbCollection) {
		List<DBObject[]> operationsList = new ArrayList<>();
		operationsList.add(operations);
		return executeAggregationList(Arrays.asList(id), operationsList, options, dbCollection);
	}

	protected QueryResponse executeAggregationList(List<? extends Object> ids, List<DBObject[]> operationsList, QueryOptions options, DBCollection dbCollection) {
		long timeStart = System.currentTimeMillis();
		QueryResponse queryResponse = new QueryResponse();
		AggregationOutput aggregationOutput;

		long dbTimeStart, dbTimeEnd;
		for (int i = 0; i < operationsList.size(); i++) {
			DBObject[] operations = operationsList.get(i);

			// Mongo aggregate method signature is :public AggregationOutput aggregate( DBObject firstOp, DBObject ... additionalOps)
			// so the operations array must be decomposed, TODO check operations length
			DBObject firstOperation = operations[0];
			DBObject[] additionalOperations = Arrays.copyOfRange(operations, 1, operations.length);

			QueryResult queryResult = new QueryResult();
			// Execute query and calculate time
			dbTimeStart = System.currentTimeMillis();
			aggregationOutput = dbCollection.aggregate(firstOperation, additionalOperations);
			//            cursor = mongoDBCollection.find(query, returnFields);
			dbTimeEnd = System.currentTimeMillis();

			BasicDBList list = new BasicDBList();
			try {
				if (aggregationOutput != null) {
					Iterator<DBObject> results = aggregationOutput.results().iterator();
					while (results.hasNext()) {
						list.add(results.next());
					}
				}
			} finally {

			}
			queryResult.setDBTime((dbTimeEnd - dbTimeStart));
			queryResult.setResult(list);    //.toString()
			//			list2.add(list);
			// Save QueryResult into QueryResponse object
			queryResponse.put(ids.get(i).toString(), queryResult);
		}
		long timeEnd = System.currentTimeMillis();

		// Check if 'metadata' field must be returned
		if (options != null && options.getBoolean("metadata", true)) {
			queryResponse.getMetadata().put("queryIds", ids);
			queryResponse.getMetadata().put("time", timeEnd - timeStart);
		} else {
			queryResponse.removeField("metadata");
		}

		return queryResponse;
	}



	//	protected List<?> execute(Criteria criteria){
	//		List<?> result = criteria.list();
	//		return result;
	//	}
	//
	//	protected List<?> executeAndClose(Criteria criteria){
	//		List<?> result = criteria.list();
	//		//		closeSession();
	//		return result;
	//	}
	//
	//
	//	protected List<?> execute(Query query){
	//		List<?> result = query.list();
	//		return result;
	//	}
	//
	//	protected List<?> executeAndClose(Query query){
	//		List<?> result = query.list();
	//		//		closeSession();
	//		return result;
	//	}

	//	protected void closeSession() {
	//		if(session != null && session.isOpen()) {
	//			session.close();
	//		}
	//	}
	//
	//	@SuppressWarnings("unchecked")
	//	protected String getDatabaseQueryCache(String key) {
	//		Criteria criteria = this.openSession().createCriteria(Metainfo.class)
	//			.add(Restrictions.eq("property", key));
	//		List<Metainfo> metaInfoList = (List<Metainfo>) executeAndClose(criteria);
	//		if(metaInfoList != null && metaInfoList.size() > 0) {
	//			return metaInfoList.get(0).getValue();
	//		}else {
	//			return null;
	//		}
	//	}
	//
	//	protected void putDatabaseQueryCache(String key, String value) {
	////		Query query = this.openSession().createQuery("insert into Metainfo (property, value) values ('"+key+"', '"+value+"')");
	////		query.executeUpdate();
	//
	//		Session session = this.openSession();
	//		session.beginTransaction();
	//		session.save( new Metainfo( key, value ) );
	//		session.getTransaction().commit();
	//		session.close();
	//	}

	/**
	 * For histograms
	 */
	protected List<IntervalFeatureFrequency> getIntervalFeatureFrequencies(Region region, int interval, List<Object[]> objectList, int numFeatures, double maxSnpsInterval) {

		int numIntervals = (region.getEnd() - region.getStart()) / interval + 1;
		List<IntervalFeatureFrequency> intervalFeatureFrequenciesList = new ArrayList<IntervalFeatureFrequency>(numIntervals);

		//		BigInteger max = new BigInteger("-1");
		//		for(int i=0; i<objectList.size(); i++) {
		//			if(((BigInteger)objectList.get(i)[1]).compareTo(max) > 0) {
		//				max = (BigInteger)objectList.get(i)[1];
		//			}
		//		}
		float maxNormValue = 1;

		if (numFeatures != 0) {
			maxNormValue = (float) maxSnpsInterval / numFeatures;
		}

		int start = region.getStart();
		int end = start + interval;
		for (int i = 0, j = 0; i < numIntervals; i++) {
			if (j < objectList.size() && ((BigInteger) objectList.get(j)[0]).intValue() == i) {
				if (numFeatures != 0) {
					intervalFeatureFrequenciesList.add(new IntervalFeatureFrequency(start, end, ((BigInteger) objectList.get(j)[0]).intValue()
							, ((BigInteger) objectList.get(j)[1]).intValue()
							, (float) Math.log(((BigInteger) objectList.get(j)[1]).doubleValue()) / numFeatures / maxNormValue));
				} else {    // no features for this chromosome
					intervalFeatureFrequenciesList.add(new IntervalFeatureFrequency(start, end, ((BigInteger) objectList.get(j)[0]).intValue()
							, ((BigInteger) objectList.get(j)[1]).intValue()
							, 0));
				}
				j++;
			} else {
				intervalFeatureFrequenciesList.add(new IntervalFeatureFrequency(start, end, i, 0, 0.0f));
			}
			//			System.out.println(intervalFeatureFrequenciesList.get(i).getStart()+":"+intervalFeatureFrequenciesList.get(i).getEnd()+":"+intervalFeatureFrequenciesList.get(i).getInterval()+":"+ intervalFeatureFrequenciesList.get(i).getAbsolute()+":"+intervalFeatureFrequenciesList.get(i).getValue());

			start += interval;
			end += interval;
		}

		return intervalFeatureFrequenciesList;
	}


	protected List<IntervalFeatureFrequency> getIntervalFeatureFrequencies(Region region, int interval, List<Object[]> objectList) {

		int numIntervals = (region.getEnd() - region.getStart()) / interval + 1;
		List<IntervalFeatureFrequency> intervalFeatureFrequenciesList = new ArrayList<IntervalFeatureFrequency>(numIntervals);

		BigInteger max = new BigInteger("-1");
		for (int i = 0; i < objectList.size(); i++) {
			if (((BigInteger) objectList.get(i)[1]).compareTo(max) > 0) {
				max = (BigInteger) objectList.get(i)[1];
			}
		}

		int start = region.getStart();
		int end = start + interval;
		for (int i = 0, j = 0; i < numIntervals; i++) {
			if (j < objectList.size() && ((BigInteger) objectList.get(j)[0]).intValue() == i) {
				intervalFeatureFrequenciesList.add(new IntervalFeatureFrequency(start, end, ((BigInteger) objectList.get(j)[0]).intValue()
						, ((BigInteger) objectList.get(j)[1]).intValue()
						, ((BigInteger) objectList.get(j)[1]).floatValue() / max.floatValue()));
				j++;
			} else {
				intervalFeatureFrequenciesList.add(new IntervalFeatureFrequency(start, end, i, 0, 0.0f));
			}
			//			System.out.println(intervalFeatureFrequenciesList.get(i).getStart()+":"+intervalFeatureFrequenciesList.get(i).getEnd()+":"+intervalFeatureFrequenciesList.get(i).getInterval()+":"+ intervalFeatureFrequenciesList.get(i).getAbsolute()+":"+intervalFeatureFrequenciesList.get(i).getValue());

			start += interval;
			end += interval;
		}

		return intervalFeatureFrequenciesList;
	}

	//	/**
	//	 * @return the sessionFactory
	//	 */
	//	public SessionFactory getSessionFactory() {
	//		return sessionFactory;
	//	}
	//
	//	/**
	//	 * @param sessionFactory the sessionFactory to set
	//	 */
	//	public void setSessionFactory(SessionFactory sessionFactory) {
	//		this.sessionFactory = sessionFactory;
	//	}

	/**
	 * @return the species
	 */
	public String getSpecies() {
		return species;
	}

	/**
	 * @param species the species to set
	 */
	public void setSpecies(String species) {
		this.species = species;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

}
