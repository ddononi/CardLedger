package kr.co.cardledger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter implements SectionIndexer {
	private final int layout;
	private List<CardData> list;
	HashMap<String, Integer> alphaIndexer;

	private LayoutInflater inflater = null;
	String[] sections;

	public ListAdapter(final Context context,
			final List<CardData> list, final int layout) {
		this.list = list;
		this.layout = layout;
		// 인플레이터 얻기
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		setIndexer(list);
	}


	/**
	 * @param list
	 */
	private void setIndexer(final List<CardData> list) {
		// 초성 인덱서 설정
		alphaIndexer = new HashMap<String, Integer>();
		int size = this.list.size();
		String name;
		String compareName = "";
		String firstChar = "";
		CardData c;
		for (int i = 0; i < size; i++) {
			c = list.get(i);
			name = c.getShop().trim();
			if(!name.equals("")){
				firstChar = name.substring(0, 1);
			}else{
				name = "";
			}
			alphaIndexer.put(firstChar, i);
			compareName = firstChar;
		}

		// 중복키 제거
		Set<String> keys = alphaIndexer.keySet();
		Iterator<String> it = keys.iterator();
		ArrayList<String> keyList = new ArrayList<String>();

		while (it.hasNext()) {
			String key = it.next();
			keyList.add(key);
		}

		Collections.sort(keyList);
		sections = new String[keyList.size()];
		keyList.toArray(sections);
	}

	public void resetList(final List<CardData> list){
		this.list = list;
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
		setIndexer(list);
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(final int pos) {
		// TODO Auto-generated method stub
		return list.get(pos);
	}

	@Override
	public long getItemId(final int pos) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	synchronized public View getView(final int pos, final View convertView,
			final ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = null;
		ViewHolder wrapper = null;

		CardData data = (CardData) getItem(pos);
		if (convertView == null) {
			// Context context = parent.getContext();
			view = inflater.inflate(layout, null);
			MyActivity.recursiveViewSetTypeFace((ViewGroup)view);
			wrapper = new ViewHolder(view);
			view.setTag(wrapper);
		} else {
			view = convertView;
			wrapper = (ViewHolder) view.getTag();
		}


		wrapper.getNameTv().setText(data.getShop());
		wrapper.getTelTv().setText(data.getPrice());

		return wrapper.getBase();
	}

	@Override
	public int getPositionForSection(final int section) {
		// TODO Auto-generated method stub
		String letter = sections[section];
		return alphaIndexer.get(letter);
	}

	@Override
	public int getSectionForPosition(final int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return sections;
	}

	/**
	 * 엘리먼트 후킹부하를 줄이기 위한 클래스
	 */
	class ViewHolder {
		private final View base;
		private TextView nameTv = null;
		private TextView telTv = null;
		private final TextView indexLabel = null;

		ViewHolder(final View base) {
			this.base = base;
		}

		View getBase() {
			return base;
		}

		TextView getNameTv() {
			if (nameTv == null) {
				nameTv = (TextView) base.findViewById(R.id.shop);
			}
			return nameTv;
		}

		TextView getTelTv() {
			if (telTv == null) {
				telTv = (TextView) base.findViewById(R.id.price);
			}
			return telTv;
		}
	}
}
