/*
 * Copyright (C) 2009 Teleca Poland Sp. z o.o. <android@teleca.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package per.sue.gear3.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Nice wrapper-abstraction around ArrayList
 * 
 * @author Lukasz Wisniewski
 *
 * @param <T>
 */
public abstract class GearListAdapter<T> extends BaseAdapter{

	private ArrayList<Boolean> checkList = new ArrayList<>();
	protected boolean isSelectModel;
	protected boolean isSingleModel;
	private ArrayList<T> selectList = new ArrayList<>();
	protected  OnArrayListAdapterChangeListener<T> onArrayListAdapterChangeListener;
	protected ArrayListAdapterListener<T> arrayListAdapterListener;
	protected  OnArrayListAdapterOptionListener<T> arrayListAdapterOptionListener;
	protected ArrayList<T> list;
	protected Context context;
	protected ListView listView;
	public GearListAdapter(Context context){
		this.context = context;
	}

	@Override
	public int getCount() {
		if(this.list != null)
			return this.list.size();
		else
			return 0;
	}

	@Override
	public T getItem(int position) {
		if(position >= this.list.size()||position<0)
			return null;
		return this.list == null ? null : this.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	abstract public View getView(int position, View convertView, ViewGroup parent);
	
	public void setList(ArrayList<T> listTmp){
		if(null != listTmp){
			this.list = new ArrayList<T>();
			for(T t : listTmp){
				if(null != t){
					this.list.add(t);
				}
			}

		checkList.clear();
		for(T bean : list){
			checkList.add(has(bean));
		}
		}
		notifyDataSetChanged();
	}

	public void addList(ArrayList<T> listTmp){
		if(this.list== null ){
			this.list = listTmp;
		}else{

			if(null == listTmp)
				listTmp = new ArrayList<T>();

			for(T t : listTmp){
				if(null != t){
					this.list.add(t);
				}
			}
			//this.list.addAll(list);
		}

		for(T bean : listTmp){
			checkList.add(has(bean));
		}
		notifyDataSetChanged();
	}

	protected boolean has(T t){
		boolean b = false;
		for(T bean : selectList){
			if(isEqual(t, bean)){
				b = true;
				break;
			}
		}
		return b;
	}

	protected int getIndexSelectLisr(T t){
		int  index = -1;
		for(int i = 0; i < selectList.size(); i++){
			T bean = selectList.get(i);
			if(isEqual(t, bean)){
				index = i;
				break;
			}
		}
		return index;
	}

	protected boolean isEqual(T itemBean, T selectedBean){
		return false;
	}



	public void addList(int postion, ArrayList<T> list){
		if(this.list == null ){
			this.list = list;
		}else{
			this.list.addAll(postion, list);
		}
		notifyDataSetChanged();
	}

	public ArrayList<T> getSelectedList(){
		ArrayList<T> list = new ArrayList<>();
		for(int i= 0; i < checkList.size() ; i++){
			if(checkList.get(i)){
				list.add(getItem(i));
			}
		}
		return list;
	}

	public T getSelectedItemForSingle(){
		T t = null;
		ArrayList<T> list = new ArrayList<>();
		for(int i= 0; i < checkList.size() ; i++){
			if(checkList.get(i)){
				list.add(getItem(i));
			}
		}
		if(list.size() > 0){
			t = list.get(0);
		}
		return t;
	}

	public int getSelectedIndexForSingle(){
		int index = -1;
		for(int i= 0; i < checkList.size() ; i++){
			if(checkList.get(i)){
				index = i;
			}
		}

		return index;
	}


	protected void setCheckChange(int position){
		if(isSingleModel){
			for(int i = 0 ; i < checkList.size() ; i++){
				checkList.set(i, false);
			}
			checkList.set(position, true);
		}else{
			checkList.set(position, !checkList.get(position));
		}

		notifyDataSetChanged();
		if(null != onArrayListAdapterChangeListener){
			onArrayListAdapterChangeListener.onItemChange(position, getItem(position));
		}
	}

	public boolean isChecked(int position){
		return  checkList.get(position);
	}

	public ArrayList<T> getList(){
		return this.list ;
	}
	
	public void setList(T[] list){
		ArrayList<T> arrayList = new ArrayList<T>(list.length);  
		for (T t : list) {  
			arrayList.add(t);  
		}  
		setList(arrayList);
	}
	
	public ListView getListView(){
		return listView;
	}
	
	public void setListView(ListView listView){
		listView = listView;
	}
	
	
	public Context getContext(){
		return context;
	}

	public  <T extends View> T findViewById(View view, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}


	public boolean isSelectModel() {
		return isSelectModel;
	}

	public void changeSelectModel(){
		isSelectModel = !isSelectModel;
		notifyDataSetChanged();
	}


	public void setSelectModel(boolean selectModel) {
		isSelectModel = selectModel;
	}

	public void setSingleModel(boolean singleModel) {
		isSingleModel = singleModel;
	}

	public interface ArrayListAdapterListener<T>{
		void onItemClick(int position, T t);
	}

	public interface OnArrayListAdapterOptionListener<T>{
		void onItemEdit(int position, T t);
		void onItemDelete(int position, T t);
	}

	public interface OnArrayListAdapterChangeListener<T>{
		void onItemChange(int position, T t);
	}


	public OnArrayListAdapterChangeListener<T> getOnArrayListAdapterChangeListener() {
		return onArrayListAdapterChangeListener;
	}

	public void setOnArrayListAdapterChangeListener(OnArrayListAdapterChangeListener<T> onArrayListAdapterChangeListener) {
		this.onArrayListAdapterChangeListener = onArrayListAdapterChangeListener;
	}

	public void setArrayListAdapterListener(ArrayListAdapterListener<T> arrayListAdapterListener) {
		this.arrayListAdapterListener = arrayListAdapterListener;
	}

	public void setOnArrayListAdapterOptionListener(OnArrayListAdapterOptionListener<T> arrayListAdapterOptionListener) {
		this.arrayListAdapterOptionListener = arrayListAdapterOptionListener;
	}

	public void setSelectList(ArrayList<T> selectList) {
		if(null == selectList){
			selectList = new ArrayList<T>();
		}
		this.selectList = selectList;
		refreshCheckedForSelectData();
	}

	private void refreshCheckedForSelectData(){
		if(null != list && list.size() > 0){
			for(int i =0; i <list.size(); i++ ){
				T t = list.get(i);
				if(has(t)){
					checkList.set(i, true);
				}else{
					checkList.set(i, false);
				}
			}
			notifyDataSetChanged();
		}
	}

	public void addSelectItem(T t){
		boolean has = has(t);
		if(!has){
			this.selectList.add(t);
		}
		refreshCheckedForSelectData();
	}

	public void clearAllSelectItems(){
		selectList.clear();
		refreshCheckedForSelectData();
	}

	public void removeSelectItem(T t){
	    int index = getIndexSelectLisr(t);
		if(index > -1){
			this.selectList.remove(index);
		}
		refreshCheckedForSelectData();
	}
}
