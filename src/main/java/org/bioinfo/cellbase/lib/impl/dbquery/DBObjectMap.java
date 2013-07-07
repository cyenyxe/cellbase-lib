package org.bioinfo.cellbase.lib.impl.dbquery;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class DBObjectMap extends LinkedHashMap<String, Object> {

	
	private static final long serialVersionUID = 3765181002574467833L;

	protected static Gson gson = new Gson();
	
	
	public DBObjectMap() {
		
	}

	public DBObjectMap(int size) {
		super(size);
	}

	public DBObjectMap(String key, Object value) {
		put(key, value);
	}
	
	public DBObjectMap(String json) {
		this.putAll(gson.fromJson(json, this.getClass()));
	}
	
	public String toJson() {
		return gson.toJson(this);
	}
	
	public String safeToString() {
		Iterator<String> iter = this.keySet().iterator();
		String key;
		StringBuilder sb = new StringBuilder("{\n");
		while(iter.hasNext()) {
			key = iter.next();
			if(!key.equals("result")) {
				sb.append("\t"+key+": " + this.get(key)+",\n");
			}else {
//				sb.append("\t"+key+": " + this.getString(key).substring(0, 10)+"...\n");
			}
		}
		sb.append("}");
		return sb.toString();
	}
	
	public void safePrint() {
		System.out.println(safeToString());
	}
	
	
	public boolean containsField(String key) {
		return this.containsKey(key);
	}
	
	public Object removeField(String key) {
		return this.remove(key);
	}
	
	
	
	
	public String getString(String field) {
		return getString(field, "");
	}

	public String getString(String field, String defaultValue) {
		if(field != null && this.containsKey(field)) {
			return (String)this.get(field);
		}
		return defaultValue;
	}
	

	public int getInt(String field) {
		return getInt(field, 0);
	}

	public int getInt(String field, int defaultValue) {
		if(field != null && this.containsKey(field)) {
			Object obj = this.get(field);
			switch(obj.getClass().getSimpleName()) {
				case "Integer":
					return (Integer)obj;
				case "Double":
					return ((Double)obj).intValue();
				case "Float":
					return ((Float)obj).intValue();	
				case "String":
					return Integer.parseInt(String.valueOf(obj));
				default:
					return defaultValue;
			}
		}
		return defaultValue;
	}


	public float getFloat(String field) {
		return getFloat(field, 0.0f);
	}

	public float getFloat(String field, float defaultValue) {
		if(field != null && this.containsKey(field)) {
			Object obj = this.get(field);
			switch(obj.getClass().getSimpleName()) {
				case "Float":
					return (Float)obj;
				case "Double":
					return ((Double)obj).floatValue();
				case "Integer":
					return ((Integer)obj).floatValue();
				case "String":
					return Float.parseFloat((String)this.get(field));
				default:
					return defaultValue;
			}
		}
		return defaultValue;
	}


	public double getDouble(String field) {
		return getDouble(field, 0.0);
	}

	public double getDouble(String field, double defaultValue) {
		if(field != null && this.containsKey(field)) {
			Object obj = this.get(field);
			switch(obj.getClass().getSimpleName()) {
				case "Double":
					return (Double)obj;
				case "Float":
					return ((Float)obj).doubleValue();
				case "Integer":
					return ((Integer)obj).doubleValue();
				case "String":
					return Double.parseDouble((String)this.get(field));
				default:
					return defaultValue;
			}			
		}
		return defaultValue;
	}

	
	public boolean getBoolean(String field) {
		return getBoolean(field, false);
	}

	public boolean getBoolean(String field, boolean defaultValue) {
		if(field != null && this.containsKey(field)) {
			Object obj = this.get(field);
			switch(obj.getClass().getSimpleName()) {
				case "Boolean":
					return (Boolean)this.get(field);
				case "String":
					return Boolean.parseBoolean((String)this.get(field));
				default:
					return defaultValue;
			}
		}
		return defaultValue;
	}

	
	public List<Object> getList(String field) {
		return getList(field, null);
	}

	public List<Object> getList(String field, List<Object> defaultValue) {
		if(field != null && this.containsKey(field)) {
			return (List<Object>)this.get(field);
		}
		return defaultValue;
	}
	
	
	public Map<String, Object> getMap(String field) {
		return getMap(field, null);
	}

	public Map<String, Object> getMap(String field, Map<String, Object> defaultValue) {
		if(field != null && this.containsKey(field)) {
			return (Map<String, Object>)this.get(field);
		}
		return defaultValue;
	}
	

}
