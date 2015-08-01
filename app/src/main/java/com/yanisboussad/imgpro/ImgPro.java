package com.yanisboussad.imgpro;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class ImgPro extends Activity implements  OnTouchListener{

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int NONE = 0;
    private int mode = NONE;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private Bitmap resultBitmap;
	private String imageName ;
	private ImageView imageView;
	private ImageEffects imageEffects = new ImageEffects();
	private Historic historicClass = new Historic();
	private ConvolutionFilters convolutionClass = new ConvolutionFilters();
	private ProgressBar progress;
    private TextView brightnessTv, contrastTv;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float d = 0f;

    private float[] lastEvent = null;
    private SeekBar brightnessSeekBar,contrastSeekBar;
    private Button applyBtn;
    private Button cancelBtn;
    @SuppressWarnings("ConstantConditions")
    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_pro);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().
                getColor(R.color.themeColor)));
        imageView = (ImageView) findViewById(R.id.imageView1);
        imageView.setDrawingCacheEnabled(true);
        brightnessSeekBar = (SeekBar) findViewById(R.id.brightSeekBar);
        contrastSeekBar = (SeekBar) findViewById(R.id.contrastSeekBar);

        applyBtn = (Button) findViewById(R.id.apply);
        cancelBtn = (Button) findViewById(R.id.cancel);
        brightnessTv = (TextView) findViewById(R.id.brightTv);
        contrastTv = (TextView) findViewById(R.id.contrastTv);

        brightnessTv.setVisibility(View.INVISIBLE);
        contrastTv.setVisibility(View.INVISIBLE);
        brightnessSeekBar.setVisibility(View.INVISIBLE);
        contrastSeekBar.setVisibility(View.INVISIBLE);
        applyBtn.setVisibility(View.INVISIBLE);
        cancelBtn.setVisibility(View.INVISIBLE);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brightnessSeekBar.setVisibility(View.INVISIBLE);
                contrastSeekBar.setVisibility(View.INVISIBLE);
                applyBtn.setVisibility(View.INVISIBLE);
                cancelBtn.setVisibility(View.INVISIBLE);
                brightnessTv.setVisibility(View.INVISIBLE);
                contrastTv.setVisibility(View.INVISIBLE);
                imageView.setImageBitmap(historicClass.getLastHistoric());

            }
        });
        progress = (ProgressBar) findViewById(R.id.progressBar1);
		progress.setVisibility(ProgressBar.INVISIBLE);
		progress.setProgressDrawable(getResources().getDrawable(R.drawable.ic_launcher));
		progress.getIndeterminateDrawable().setColorFilter(getResources().
                        getColor(R.color.themeColor),android.graphics.PorterDuff.Mode.MULTIPLY);

        imageView.setOnTouchListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.img, menu);

        return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Bitmap bmp ;
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
           
            bmp = BitmapFactory.decodeFile(picturePath);
            imageName = new File(picturePath).getName();
            int screenWidth = imageView.getWidth();
            int screenHeight = imageView.getHeight();



            RectF drawableRect = new RectF(0, 0, bmp.getWidth(), bmp.getHeight());
            RectF viewRect = new RectF(0, 0, screenWidth, screenHeight);
            matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageBitmap(bmp);
            imageView.setScaleType(ImageView.ScaleType.MATRIX);

            historicClass.setNewHistoric(bmp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       
        int id = item.getItemId();
        if (id == R.id.about) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.aboutDescription);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        }

        if (id == R.id.openImage) {

            try {
                Intent i = new Intent(Intent.ACTION_PICK,
                   MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Please Open an Image!", Toast.LENGTH_SHORT).show();
            }


            return true;
        }

        if (id == R.id.save) {
            try {
                if(historicClass.getCurrentHistoric() != null){
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root);
                File file = new File (myDir, "this is a Filtered-"+imageName);

                if (file.exists ()) file.delete ();
                try {
                       FileOutputStream out = new FileOutputStream(file);
                       historicClass.getCurrentHistoric().compress(Bitmap.CompressFormat.JPEG, 90, out);
                       out.flush();
                       out.close();

                } catch (Exception e) {
                       e.printStackTrace();
                }}
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Please Open an Image!", Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        if (id == R.id.greyscale) {
            if (Historic.hist !=-1) {
                progress.setVisibility(ProgressBar.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        resultBitmap = imageEffects.Greyscale(historicClass.getCurrentHistoric());
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                imageView.setImageBitmap(resultBitmap);
                                progress.setVisibility(ProgressBar.INVISIBLE);
                            }
                        });
                        historicClass.setNewHistoric(imageEffects.setBmp());
}
                }).start();
            } else {
                Toast.makeText(getApplicationContext(), "Please Open an Image!", Toast.LENGTH_SHORT).show();
            }


            return true;
        }

        if (id == R.id.edge) {
            if (Historic.hist !=-1) {
                progress.setVisibility(ProgressBar.VISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        resultBitmap = convolutionClass.Filter(historicClass.getCurrentHistoric(), ConvolutionFilters.filterType.EDGE_DETECT);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                imageView.setImageBitmap(resultBitmap);
                                progress.setVisibility(ProgressBar.INVISIBLE);

                            }
                        });
                        historicClass.setNewHistoric(convolutionClass.setBmp());
                    }
                }).start();
            } else {
                Toast.makeText(getApplicationContext(), "Please Open an Image!", Toast.LENGTH_SHORT).show();
            }


            return true;
        }

        if (id == R.id.sharpen) {
            if (Historic.hist !=-1) {
                progress.setVisibility(ProgressBar.VISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        resultBitmap = convolutionClass.Filter(historicClass.getCurrentHistoric(), ConvolutionFilters.filterType.SHARP);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                imageView.setImageBitmap(resultBitmap);
                                progress.setVisibility(ProgressBar.INVISIBLE);

                            }
                        });
                        historicClass.setNewHistoric(convolutionClass.setBmp());
                    }
                }).start();
            } else {
                Toast.makeText(getApplicationContext(), "Please Open an Image!", Toast.LENGTH_SHORT).show();

            }



            return true;
        }
        if (id == R.id.gaussian) {
            if (Historic.hist !=-1) {
                progress.setVisibility(ProgressBar.VISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        resultBitmap = convolutionClass.Filter(historicClass.getCurrentHistoric(), ConvolutionFilters.filterType.BLUR);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                imageView.setImageBitmap(resultBitmap);
                                progress.setVisibility(ProgressBar.INVISIBLE);

                            }
                        });
                        historicClass.setNewHistoric(convolutionClass.setBmp());
                    }
                }).start();
            } else {
                Toast.makeText(getApplicationContext(), "Please Open an Image!", Toast.LENGTH_SHORT).show();
            }


            return true;
        }

        if (id == R.id.invert) {
            if (Historic.hist !=-1) {
                progress.setVisibility(ProgressBar.VISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        resultBitmap = imageEffects.Invert(historicClass.getCurrentHistoric());
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                progress.setVisibility(ProgressBar.INVISIBLE);
                                imageView.setImageBitmap(resultBitmap);
                            }
                        });
                        historicClass.setNewHistoric(imageEffects.setBmp());
                        }
                }).start();
            } else {
                Toast.makeText(getApplicationContext(), "Please Open an Image!", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
        if (id == R.id.sepia) {
            if (Historic.hist !=-1) {
                progress.setVisibility(ProgressBar.VISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        resultBitmap = imageEffects.SepiaEffect(historicClass.getCurrentHistoric());
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                progress.setVisibility(ProgressBar.INVISIBLE);
                                imageView.setImageBitmap(resultBitmap);
                            }
                        });
                        historicClass.setNewHistoric(imageEffects.setBmp());
                    }
                }).start();
            } else {
                Toast.makeText(getApplicationContext(), "Please Open an Image!", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
        if (id == R.id.Contrast) {
            if (Historic.hist !=-1) {
                applyBtn.setVisibility(View.VISIBLE);
                cancelBtn.setVisibility(View.VISIBLE);

                contrastSeekBar.setVisibility(View.VISIBLE);

                contrastTv.setVisibility(View.VISIBLE);
                contrastSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                        progress.setVisibility(ProgressBar.VISIBLE);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                resultBitmap = imageEffects.Contrast(historicClass.getCurrentHistoric(), contrastSeekBar.getProgress() + 1);
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        progress.setVisibility(ProgressBar.INVISIBLE);
                                        imageView.setImageBitmap(resultBitmap);

                                    }
                                });

                            }
                        }).start();


                    }

            });
                applyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        historicClass.setNewHistoric(imageEffects.setBmp());
                        contrastSeekBar.setVisibility(View.INVISIBLE);
                        applyBtn.setVisibility(View.INVISIBLE);
                        cancelBtn.setVisibility(View.INVISIBLE);
                        contrastTv.setVisibility(View.INVISIBLE);

                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Please Open an Image!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        if (id == R.id.bright) {

            if (Historic.hist !=-1) {
                applyBtn.setVisibility(View.VISIBLE);
                cancelBtn.setVisibility(View.VISIBLE);
                brightnessSeekBar.setVisibility(View.VISIBLE);
                brightnessTv.setVisibility(View.VISIBLE);
                brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                        progress.setVisibility(ProgressBar.VISIBLE);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                resultBitmap = imageEffects.Brightness(historicClass.getCurrentHistoric(), brightnessSeekBar.getProgress() * 50 - 250);
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        progress.setVisibility(ProgressBar.INVISIBLE);
                                        imageView.setImageBitmap(resultBitmap);
                                    }
                                });

                            }
                        }).start();


                    }

                });

                applyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        historicClass.setNewHistoric(imageEffects.setBmp());
                        brightnessSeekBar.setVisibility(View.INVISIBLE);
                        applyBtn.setVisibility(View.INVISIBLE);
                        cancelBtn.setVisibility(View.INVISIBLE);
                        brightnessTv.setVisibility(View.INVISIBLE);

                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Please Open an Image!", Toast.LENGTH_SHORT).show();

            }


            return true;
        }
        if (id == R.id.Relief) {
            if (Historic.hist !=-1) {
                progress.setVisibility(ProgressBar.VISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        resultBitmap = convolutionClass.Filter(historicClass.getCurrentHistoric(), ConvolutionFilters.filterType.RELIEF);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                imageView.setImageBitmap(resultBitmap);
                                progress.setVisibility(ProgressBar.INVISIBLE);

                            }
                        });
                        historicClass.setNewHistoric(convolutionClass.setBmp());
                    }
                }).start();
            } else {
                Toast.makeText(getApplicationContext(), "Please Open an Image!", Toast.LENGTH_SHORT).show();

            }


            return true;
        }
        if (id == R.id.undo) {
            if (Historic.hist !=-1) {
                imageView.setImageBitmap(historicClass.getLastHistoric());
            }

        }
        if (id == R.id.redo) {
            if (Historic.hist !=-1) {
                imageView.setImageBitmap(historicClass.getNextHistoric());
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float newRot;
        // handle touch events here
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                lastEvent = new float[4];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);
                d = rotation(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG ) {
                    matrix.set(savedMatrix);
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;
                    matrix.postTranslate(dx, dy);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = (newDist / oldDist);
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                    if (lastEvent != null && event.getPointerCount() == 3) {
                        newRot = rotation(event);
                        float r = newRot - d;
                        float[] values = new float[9];
                        matrix.getValues(values);
                        float tx = values[2];
                        float ty = values[5];
                        float sx = values[0];
                        float xc = (view.getWidth() / 2) * sx;
                        float yc = (view.getHeight() / 2) * sx;
                        matrix.postRotate(r, tx + xc, ty + yc);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix);
        return true;
    }


    }

