package org.bioinfo.cellbase.lib.impl.mongodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bioinfo.cellbase.lib.api.GeneDBAdaptor;
import org.bioinfo.cellbase.lib.common.Position;
import org.bioinfo.cellbase.lib.common.Region;
import org.bioinfo.cellbase.lib.common.core.Gene;
import org.bioinfo.cellbase.lib.impl.dbquery.QueryOptions;
import org.bioinfo.cellbase.lib.impl.dbquery.QueryResponse;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

//import org.bioinfo.infrared.core.cellbase.Gene;

public class GeneMongoDBAdaptor extends MongoDBAdaptor implements GeneDBAdaptor {


	public GeneMongoDBAdaptor(DB db) {
		super(db);
	}

	public GeneMongoDBAdaptor(DB db, String species, String version) {
		super(db, species, version);
		mongoDBCollection = db.getCollection("core");

		logger.info("In GeneMongoDBAdaptor constructor");
	}

	//	private List<Gene> executeQuery(DBObject query, List<String> excludeFields) {
	//		List<Gene> result = null;
	//		
	//		DBCursor cursor = null;
	//		if (excludeFields != null && excludeFields.size() > 0) {
	//			BasicDBObject returnFields = new BasicDBObject("_id", 0);
	//			for (String field : excludeFields) {
	//				returnFields.put(field, 0);
	//			}
	//			cursor = mongoDBCollection.find(query, returnFields);
	//		} else {
	//			cursor = mongoDBCollection.find(query);
	//		}
	//		
	//		try {
	//			if (cursor != null) {
	//				result = new ArrayList<Gene>(cursor.size());
	//				Gson jsonObjectMapper = new Gson();
	//				Gene gene;
	//				while (cursor.hasNext()) {
	//					gene = (Gene) jsonObjectMapper.fromJson(cursor.next().toString(), Gene.class);
	//					result.add(gene);
	//				}
	//			}
	//		} finally {
	//			cursor.close();
	//		}
	//		return result;
	//	}


	@Override
	public QueryResponse getAll(QueryOptions options) {
		QueryBuilder builder = new QueryBuilder();

		List<Object> biotypes = options.getList("biotypes", null);
		if (biotypes != null && biotypes.size() > 0) {
			BasicDBList biotypeIds = new BasicDBList();
			biotypeIds.addAll(biotypes);
			builder = builder.and("biotype").in(biotypeIds);
		}

//		options = addExcludeReturnFields("transcripts", options);
		return executeQuery("result", builder.get(), options);
	}

	@Override
	public QueryResponse next(String id, QueryOptions options) {
		// TODO Auto-generated method stub
		QueryBuilder builder = QueryBuilder.start("transcripts.xrefs.id").is(id);
		DBObject returnFields = getReturnFields(options);
		BasicDBList list = executeFind(builder.get(), returnFields, options);
		if(list != null && list.size() > 0) {
			DBObject gene = (DBObject) list.get(0);
            System.out.println(Integer.parseInt(gene.get("start").toString()));
			return next((String)gene.get("chromosome"), Integer.parseInt(gene.get("start").toString()), options);
		}
		return null;
	}

    // INFO:
    // next(chromosome, position) method has been moved to MongoDBAdaptor class

	@Override
	public QueryResponse getAllById(String id, QueryOptions options) {
		QueryBuilder builder = QueryBuilder.start("transcripts.xrefs.id").is(id);

		options = addExcludeReturnFields("transcripts", options);
		return executeQuery("result", builder.get(), options);
	}

	@Override
	public QueryResponse getAllByIdList(List<String> idList, QueryOptions options) {
		//		QueryBuilder builder = QueryBuilder.start("transcripts.xrefs.id").in(idList);

		List<DBObject> queries = new ArrayList<>();
		for(String id: idList) {
			QueryBuilder builder = QueryBuilder.start("transcripts.xrefs.id").is(id);
			queries.add(builder.get());
		}

		options = addExcludeReturnFields("transcripts", options);
		return executeQueryList(idList, queries, options);
	}


	public QueryResponse getAllEnsemblIds() {
		BasicDBObject query = new BasicDBObject();

		query.put("id", "ENSG00000260748");
		BasicDBObject returnFields = new BasicDBObject();
		returnFields.put("transcripts.xrefs", 1);
		DBCursor cursor = mongoDBCollection.find(query);
		DBObject res = cursor.next();
		System.out.println(res.get("transcripts"));

		//		BasicDBObject returnFields = new BasicDBObject();
		// returnFields.put("id", 1);
		//		returnFields.put("_id", 0);
		//		returnFields.put("id", 1);
		//		returnFields.put("name", 1);

		query = new BasicDBObject();
		BasicDBObject orderBy = new BasicDBObject();
		orderBy.put("id", 1);

		QueryResponse result = executeQuery("result", query, null);
		result.safePrint();
		//		List<String> idList = new ArrayList<String>(65000);
		//		DBCursor cursor = mongoDBCollection.find(query, returnFields).sort(orderBy);
		//		
		//		QueryResult result = new QueryResult();
		//		
		//		
		//		DBObject explain = cursor.explain();
		//		
		//		
		//		//		((BasicBSONList)cursor).get("id");
		////		DBObject explain = cursor.explain();
		//		long start1 = System.currentTimeMillis();
		//		BasicDBList list = new BasicDBList();
		////		BasicBSONList list1 = new BasicBSONList();
		//		while(cursor.hasNext()) {
		//			list.add(cursor.next());
		////			list1.add(cursor.next());
		////			idList.add(obj.get("id").toString());
		//		}
		//		result.setResult(list.toString());
		//		long end1 = System.currentTimeMillis();
		//		System.out.println("DBCursor to JSON1:\n"+(end1-start1));
		//		cursor.close();
		//		
		//		result.setDBTime(explain.get("millis"));
		//System.out.println(result.safeToString());
		return result;
	}




	//	@Override
	//	public List<Gene> getByXref(String xref, List<String> exclude) {
	//		BasicDBObject query = new BasicDBObject("transcripts.xrefs.id", xref.toUpperCase());
	//		return executeQuery(query, exclude);
	//	}
	//
	//    @Override
	//    public List<List<Gene>> getByXrefList(List<String> xrefList, List<String> exclude) {
	//        List<List<Gene>> genes = new ArrayList<List<Gene>>(xrefList.size());
	//        for (String name : xrefList) {
	//            genes.add(getByXref(name, exclude));
	//        }
	//        return genes;
	//    }

	@Override
	public QueryResponse getAllByPosition(String chromosome, int position, QueryOptions options) {
		return getAllByRegion(chromosome, position, position, options);
	}

	@Override
	public QueryResponse getAllByPosition(Position position, QueryOptions options) {
		return getAllByRegion(new Region(position.getChromosome(), position.getPosition(), position.getPosition()), options);
	}

	@Override
	public QueryResponse getAllByPositionList(List<Position> positionList, QueryOptions options) {
		List<Region> regions = new ArrayList<>();
		for(Position position: positionList) {
			regions.add(new Region(position.getChromosome(), position.getPosition(), position.getPosition()));
		}
		return getAllByRegionList(regions, options);
	}


	@Override
	public QueryResponse getAllByRegion(String chromosome, int start, int end, QueryOptions options) {
		return getAllByRegion(new Region(chromosome, start, end), options);
	}


	@Override
	public QueryResponse getAllByRegion(Region region, QueryOptions options) {
		QueryBuilder builder = QueryBuilder.start("chromosome").is(region.getChromosome()).and("end")
				.greaterThan(region.getStart()).and("start").lessThan(region.getEnd());

		List<Object> biotypes = options.getList("biotypes", null);
		if (biotypes != null && biotypes.size() > 0) {
			BasicDBList biotypeIds = new BasicDBList();
			biotypeIds.addAll(biotypes);
			builder = builder.and("biotype").in(biotypeIds);
		}

		options = addExcludeReturnFields("transcripts", options);
		return executeQuery(region.toString(), builder.get(), options);
	}

	@Override
	public QueryResponse getAllByRegionList(List<Region> regions, QueryOptions options) {
		List<DBObject> queries = new ArrayList<>();

		List<Object> biotypes = options.getList("biotypes", null);
		BasicDBList biotypeIds = new BasicDBList();
		if (biotypes != null && biotypes.size() > 0) {
			biotypeIds.addAll(biotypes);
		}

		List<String> ids = new ArrayList<>(regions.size());
		for(Region region: regions) {
			QueryBuilder builder = QueryBuilder.start("chromosome").is(region.getChromosome()).and("end")
					.greaterThan(region.getStart()).and("start").lessThan(region.getEnd());
			if(biotypeIds.size() > 0) {
				builder = builder.and("biotype").in(biotypeIds);				
			}
			queries.add(builder.get());
			ids.add(region.toString());
		}

		options = addExcludeReturnFields("transcripts", options);
		return executeQueryList(ids, queries, options);
	}



	@Override
	public List<Gene> getAllByCytoband(String chromosome, String cytoband) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Gene> getAllBySnpId(String snpId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<List<Gene>> getAllBySnpIdList(List<String> snpIdList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Gene> getAllByTf(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<List<Gene>> getAllByTfList(List<String> idList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Gene> getAllByTfName(String tfName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<List<Gene>> getAllByTfNameList(List<String> tfNameList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Gene> getAllTargetsByTf(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<List<Gene>> getAllTargetsByTfList(List<String> idList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Gene> getAllByMiRnaMature(String mirbaseId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<List<Gene>> getAllByMiRnaMatureList(List<String> mirbaseIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Gene> getAllTargetsByMiRnaMature(String mirbaseId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<List<Gene>> getAllTargetsByMiRnaMatureList(List<String> mirbaseIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAllIntervalFrequencies(Region region, int interval) {

		BasicDBObject start = new BasicDBObject("$gt",region.getStart());
		start.append("$lt",region.getEnd());

		BasicDBList andArr = new BasicDBList();
		andArr.add(new BasicDBObject("chromosome",region.getChromosome()));
		andArr.add(new BasicDBObject("start",start));

		BasicDBObject match = new BasicDBObject("$match",new BasicDBObject("$and",andArr));



		BasicDBList divide1 = new BasicDBList();
		divide1.add("$start");
		divide1.add(interval);

		BasicDBList divide2 = new BasicDBList();
		divide2.add(new BasicDBObject("$mod",divide1));
		divide2.add(interval);

		BasicDBList subtractList = new BasicDBList();
		subtractList.add(new BasicDBObject("$divide",divide1));
		subtractList.add(new BasicDBObject("$divide",divide2));


		BasicDBObject substract = new BasicDBObject("$subtract",subtractList);

		DBObject totalCount = new BasicDBObject("$sum",1);

		BasicDBObject g = new BasicDBObject("_id",substract);
		g.append("features_count",totalCount);
		BasicDBObject group = new BasicDBObject("$group",g);

		BasicDBObject sort = new BasicDBObject("$sort",new BasicDBObject("_id",1));

		AggregationOutput output = mongoDBCollection.aggregate(match, group, sort);

		System.out.println(output.getCommand());

		HashMap<Long,DBObject> ids = new HashMap<>();
		BasicDBList resultList = new BasicDBList();

		for (DBObject intervalObj : output.results()){
			Long _id = Math.round((Double)intervalObj.get("_id"));//is double

			DBObject intervalVisited = ids.get(_id);
			if(intervalVisited == null){
				intervalObj.put("_id",_id);
				intervalObj.put("start",getChunkStart(_id.intValue(), interval));
				intervalObj.put("end",getChunkEnd(_id.intValue(), interval));
				intervalObj.put("features_count",Math.log((int)intervalObj.get("features_count")));
				ids.put(_id,intervalObj);
				resultList.add(intervalObj);
			}else{
				Double sum = (Double)intervalVisited.get("features_count") + Math.log((int)intervalObj.get("features_count"));
				intervalVisited.put("features_count", sum.intValue());
			}
		}
		return resultList.toString();

		//		QueryBuilder builder = QueryBuilder.start("chromosome").is(region.getChromosome()).and("end")
		//				.greaterThan(region.getStart()).and("start").lessThan(region.getEnd());
		//
		//		int numIntervals = (region.getEnd() - region.getStart()) / interval + 1;
		//		int[] intervalCount = new int[numIntervals];
		//
		//		List<Gene> genes = executeQuery(builder.get(),
		//				Arrays.asList("transcripts,id,name,biotype,status,chromosome,end,strand,source,description"));
		//
		//		System.out.println("GENES index");
		//		System.out.println("numIntervals: " + numIntervals);
		//		for (Gene gene : genes) {
		//			System.out.print("gs:" + gene.getStart() + " ");
		//			if (gene.getStart() >= region.getStart() && gene.getStart() <= region.getEnd()) {
		//				int intervalIndex = (gene.getStart() - region.getStart()) / interval; // truncate
		//				System.out.print(intervalIndex + " ");
		//				intervalCount[intervalIndex]++;
		//			}
		//		}
		//		System.out.println("GENES index");
		//
		//		int intervalStart = region.getStart();
		//		int intervalEnd = intervalStart + interval - 1;
		//		BasicDBList intervalList = new BasicDBList();
		//		for (int i = 0; i < numIntervals; i++) {
		//			BasicDBObject intervalObj = new BasicDBObject();
		//			intervalObj.put("start", intervalStart);
		//			intervalObj.put("end", intervalEnd);
		//			intervalObj.put("interval", i);
		//			intervalObj.put("value", intervalCount[i]);
		//			intervalList.add(intervalObj);
		//			intervalStart = intervalEnd + 1;
		//			intervalEnd = intervalStart + interval - 1;
		//		}
		//
		//		System.out.println(region.getChromosome());
		//		System.out.println(region.getStart());
		//		System.out.println(region.getEnd());
		//		return intervalList.toString();

	}

	private int getChunkId(int position, int chunksize){
		return position/chunksize;
	}
	private int getChunkStart(int id, int chunksize){
		return  (id==0) ? 1 : id*chunksize;
	}
	private int getChunkEnd(int id, int chunksize){
		return (id*chunksize)+chunksize-1;
	}


//	@Override
//	public QueryResult getAll() {
//		BasicDBObject query = new BasicDBObject();
//		//		return executeQuery("result", query, Arrays.asList("_id"), null);
//		return null;
//	}

	@Override
	public List<String> getAllIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getInfo(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getInfoByIdList(List<String> idList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getFullInfo(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getFullInfoByIdList(List<String> idList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Region getRegionById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Region> getAllRegionsByIdList(List<String> idList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSequenceById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllSequencesByIdList(List<String> idList) {
		// TODO Auto-generated method stub
		return null;
	}

}
