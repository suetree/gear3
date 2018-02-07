package per.sue.gear3.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.squareup.picasso.Picasso;

import per.sue.gear3.R;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


public class ImageShowInBigFragment extends Fragment {
	private String mImageUrl;
	private ImageView mImageView;

	public static Bundle newInstanceBundle(String imageUrl) {
		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		return args;
	}

	public static ImageShowInBigFragment newInstance(String imageUrl) {
		final ImageShowInBigFragment f = new ImageShowInBigFragment();
		f.setArguments(newInstanceBundle(imageUrl));
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url") : null;

	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View layout = inflater.inflate(R.layout.fragment_show_big_image, container, false);
		mImageView  = (PhotoView)layout.findViewById(R.id.photoView);
		mImageView.setScaleType(ScaleType.CENTER_INSIDE);
		if (null != mImageUrl && !"".equals(mImageUrl)) {
			Picasso.with(getContext()).load(mImageUrl).into(mImageView);
		}
		return layout;
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);



	}

}
