package metropolia.fi.suondbubbles.animations;

import android.graphics.Matrix;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;

/**
 * Created by Vallo on 11/18/2015.
 */
public class HorizontalLineAnimation extends TranslateAnimation{

    private float matrixValues[] = new float[9];
    private float actualDy;
    private UpdateListener updateListener;

    public interface UpdateListener{
        void onTranslationYUpdate(float y);
    }

    public void setUpdateListener(UpdateListener updateListener){
        this.updateListener = updateListener;
    }

    public HorizontalLineAnimation(int fromXType, float fromXValue, int toXType, float toXValue, int fromYType, float fromYValue, int toYType, float toYValue) {
        super(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue, toYType, toYValue);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);

        t.getMatrix().getValues(matrixValues);
        actualDy = matrixValues[Matrix.MTRANS_Y];
        updateListener.onTranslationYUpdate(actualDy);
    }
}
