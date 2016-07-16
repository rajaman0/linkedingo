package xyz.chiragtoprani.linkedingo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;

/*
 * Portions (c) 2009 Google, Inc.
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
 *
 * @author Coby Plain coby.plain@gmail.com, Ali Muzaffar ali@muzaffar.me
 */

public class DrawSurfaceView extends View {
	Point me = new Point(-33.870932d, 151.204727d, "Me");
	Paint mPaint = new Paint();
	private double OFFSET = 0d;
	private double screenWidth, screenHeight = 0d;
	private Bitmap[] mSpots, mBlips;
	private Bitmap mRadar;
    private int count = 0;
    private boolean draw;

	public static ArrayList<Point> props = new ArrayList<Point>();
	static {
		props.add(new Point(90d, 110.8000, "North Pole"));
		props.add(new Point(-90d, -110.8000, "South Pole"));
		props.add(new Point(-45d, 144.8000, "Prithvi"));
		props.add(new Point(-33.870932d, 151.8000, "East"));
		props.add(new Point(-33.870932d, 150.8000, "West"));
	}

	public DrawSurfaceView(Context c, Paint paint) {
		super(c);
	}
    // Retrieves an image specified by the URL, displays it in the UI.


	public static Bitmap drawableToBitmap (Drawable drawable) {
		Bitmap bitmap = null;

		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			if(bitmapDrawable.getBitmap() != null) {
				return bitmapDrawable.getBitmap();
			}
		}

		if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
			bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
		} else {
			bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		}

		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	public DrawSurfaceView(Context context, AttributeSet set) {
		super(context, set);
		mPaint.setColor(Color.GREEN);
		mPaint.setTextSize(50);
		mPaint.setStrokeWidth(DpiUtils.getPxFromDpi(getContext(), 2));
		mPaint.setAntiAlias(true);
		
		mRadar = BitmapFactory.decodeResource(context.getResources(), R.drawable.radar);
		
		mSpots = new Bitmap[props.size()];
		for (int i = 0; i < mSpots.length; i++) 
			mSpots[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.dot);

		mBlips = new Bitmap[props.size()];
		for (int i = 0; i < mBlips.length; i++)
		//	mBlips[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.blip);
            new ImageRequest("http://a1.files.biography.com/image/upload/c_fill,cs_srgb,dpr_1.0,g_face,h_300,q_80,w_300/MTIwNjA4NjMzNzYwMjg2MjIw.jpg",
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap blip) {

                        mBlips[count ++] = blip;
                        Log.v("Number", "count: + " + count);
                        Log.v("Number", "length: + " + mBlips.length);
                        if (count == mBlips.length) {
                            count = 0;
                            draw = true;
                        }
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.v("Number", "error");
                    }
                });
        Log.v("Number", "error with drawer");
    }

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.d("onSizeChanged", "in here w=" + w + " h=" + h);
		screenWidth = (double) w;
		screenHeight = (double) h;
	}


	@Override
	protected void onDraw(Canvas canvas) {
        if (!draw) return;
		canvas.drawBitmap(mRadar, 0, 0, mPaint);

		int radarCentreX = mRadar.getWidth() / 2;
		int radarCentreY = mRadar.getHeight() / 2;

		for (int i = 0; i < mBlips.length; i++) {
            Bitmap blip = mBlips[i];
            // Drawable d = LoadImageFromWebOperations("http://a1.files.biography.com/image/upload/c_fill,cs_srgb,dpr_1.0,g_face,h_300,q_80,w_300/MTIwNjA4NjMzNzYwMjg2MjIw.jpg");


            Bitmap spot = mSpots[i];
			Point u = props.get(i);
			double dist = distInMetres(me, u);

			if (blip == null || spot == null)
				continue;

			if(dist > 70)
				dist = 70; //we have set points very far away for demonstration

			double angle = bearing(me.latitude, me.longitude, u.latitude, u.longitude) - OFFSET;
			double xPos, yPos;

			if(angle < 0)
				angle = (angle+360)%360;

			xPos = Math.sin(Math.toRadians(angle)) * dist;
			yPos = Math.sqrt(Math.pow(dist, 2) - Math.pow(xPos, 2));

			if (angle > 90 && angle < 270)
				yPos *= -1;

			double posInPx = angle * (screenWidth / 90d);

			int blipCentreX = blip.getWidth() / 2;
			int blipCentreY = blip.getHeight() / 2;

			xPos = xPos - blipCentreX;
			yPos = yPos + blipCentreY;
			canvas.drawBitmap(blip, (radarCentreX + (int) xPos), (radarCentreY - (int) yPos), mPaint); //radar blip

			//reuse xPos
			int spotCentreX = spot.getWidth() / 2;
			int spotCentreY = spot.getHeight() / 2;
			xPos = posInPx - spotCentreX;

			if (angle <= 45)
				u.x = (float) ((screenWidth / 2) + xPos);

			else if (angle >= 315)
				u.x = (float) ((screenWidth / 2) - ((screenWidth*4) - xPos));

			else
				u.x = (float) (float)(screenWidth*9); //somewhere off the screen

			u.y = (float)screenHeight/2 + spotCentreY;
			canvas.drawBitmap(spot, u.x, u.y, mPaint); //camera spot
			canvas.drawText(u.description, u.x, u.y, mPaint); //text
		}
	}

	public void setOffset(float offset) {
		this.OFFSET = offset;
	}

	public void setMyLocation(double latitude, double longitude) {
		me.latitude = latitude;
		me.longitude = longitude;
	}

	protected double distInMetres(Point me, Point u) {

		double lat1 = me.latitude;
		double lng1 = me.longitude;

		double lat2 = u.latitude;
		double lng2 = u.longitude;

		double earthRadius = 6371;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		return dist * 1000;
	}

	protected static double bearing(double lat1, double lon1, double lat2, double lon2) {
		double longDiff = Math.toRadians(lon2 - lon1);
		double la1 = Math.toRadians(lat1);
		double la2 = Math.toRadians(lat2);
		double y = Math.sin(longDiff) * Math.cos(la2);
		double x = Math.cos(la1) * Math.sin(la2) - Math.sin(la1) * Math.cos(la2) * Math.cos(longDiff);

		double result = Math.toDegrees(Math.atan2(y, x));
		return (result+360.0d)%360.0d;
	}
}
