package com.ipph.migratecore.deal.split;

import java.util.ArrayList;
import java.util.List;

import com.ipph.migratecore.deal.exception.SplitException;

public class CharacterSpliter implements Spliter{

	@Override
	public List<Object> split(String args, Object value) throws SplitException {
		List<Object> result=new ArrayList<>();
		
		if(null!=args&&!"".equals(args.trim())){
			String temp=(String) value;
			String separators=args.trim();
			String[] arr=temp.split(separators);
			if(arr!=null){
				for(String s:arr){
					result.add(s);
				}
			}
		}
		
		if(result.size()==0){
			if(null!=value&&!"".equals(value))
			result.add(value);
		}
		return result;
	}

}
