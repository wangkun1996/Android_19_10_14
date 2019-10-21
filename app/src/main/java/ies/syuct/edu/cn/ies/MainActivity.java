package ies.syuct.edu.cn.ies;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import ies.syuct.edu.cn.domain.StudentBean;
import ies.syuct.edu.cn.utils.HttpUtilsHttpClient;

/**
 * 第一页，登录页面
 */
public class MainActivity extends AppCompatActivity {

    private ToggleButton toggleButton;
    private EditText userName;
    private EditText userPassword;
    private TextView forgetPassword;
    private Button login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userName = findViewById(R.id.et_user_name);
        userPassword = findViewById(R.id.et_user_pwd);
        //监听小眼睛的变化
        toggleButton = findViewById(R.id.togglebutton);
        toggleButton.setOnCheckedChangeListener(new ToggleButtonClick());
        //忘记密码下划线
        forgetPassword = findViewById(R.id.forget);
        forgetPassword.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        //登录按钮
        login = findViewById(R.id.login);
        //验证登录
        login.setOnClickListener(new LoginButtonClick());


    }



    //密码可见性监听事件
    private class ToggleButtonClick implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton compoundButton,boolean ischecked) {
            //判断事件的选中状态
            if(ischecked){//显示密码
                userPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{//隐藏密码
                userPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            //每次显示或关闭后  密码显示的编辑线不统一再最后，下面是为了同一
            userPassword.setSelection(userPassword.length());
        }

    }
    //登录按钮点击事件
    private class LoginButtonClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //换背景
            login.setBackgroundResource(R.drawable.login1);
            //发送登录请求http ------    测试连接
            //开启线程,4.0以后不支持主线程进行耗时操作
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String url=HttpUtilsHttpClient.BASE_URL+"LoginServlet?username="+userName.getText().toString().trim()+"&password="+userPassword.getText().toString().trim();

                    //请求，返回json
                    String result = HttpUtilsHttpClient.getRequest(url);
                    Message msg = new Message();
                    msg.what=0x11;
                    Bundle data=new Bundle();
                    data.putString("result",result);
                    msg.setData(data);


                    //？？？？？
                    handler.sendMessage(msg);
                }

                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //测试是否能连接，不传输有效数据
                        if (msg.what == 0x11) {
                            Bundle data = msg.getData();
                            String key = data.getString("result");//得到json返回的json数据
//                                   Toast.makeText(MainActivity.this,key,Toast.LENGTH_LONG).show();
                            //StudentBean bean =new StudentBean();


                            try {
                                JSONObject json = new JSONObject(key);
                                String result = (String) json.get("result0");
                                String res = (String) json.get("result1");
                                //StudentBean ans = (StudentBean) json.get("result2");
                                if ("success".equals(result))
                                //if (true)
                                {
                                    Toast.makeText(MainActivity.this, "登录成功==", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(MainActivity.this, IndexActivity.class);
                                    intent.putExtra("","yes");//登录信息传递
                                    startActivity(intent);
                                } else if ("error".equals(result)) {
                                    Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };

            }).start();
        }
    }
}

