package com.stur.lib.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.stur.lib.R;

import java.util.ArrayList;

/**
 * Created by Sturmegezhutz on 2018/2/2.
 */

public class StDialog {

    /* 普通对话框
     */
    public static void showNormalDialog(Context context, int icon, String title,
                                        String msg, String pos, String neg,
                                        DialogInterface.OnClickListener poslisen,
                                        DialogInterface.OnClickListener neglisen){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
        normalDialog.setIcon(icon);
        normalDialog.setTitle(title);
        normalDialog.setMessage(msg);
        normalDialog.setPositiveButton(pos, poslisen);
        if (neg != null) {
            normalDialog.setNegativeButton(neg, neglisen);
        }
        normalDialog.show();
    }

    public static void selectActivity(final Context context, String pos, String neg, final Class posAct, final Class netAct) {
        new AlertDialog.Builder(context).setMessage("Selecting:")
                .setPositiveButton(pos, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        context.startActivity(new Intent(context, posAct));
                    }
                }).setNegativeButton(neg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                context.startActivity(new Intent(context, netAct));
            }
        }).create().show();
    }

    /* 3个按钮的对话框
    @setNeutralButton 设置中间的按钮
    若只需一个按钮，仅设置 setPositiveButton 即可
    */
    public static void showMultiBtnDialog(Context context){
        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
        normalDialog.setIcon(R.drawable.icon_bt);
        normalDialog.setTitle("我是一个普通Dialog").setMessage("你要点击哪一个按钮呢?");
        normalDialog.setPositiveButton("按钮1",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });
        normalDialog.setNeutralButton("按钮2",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });
        normalDialog.setNegativeButton("按钮3", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });
        normalDialog.show();
    }

    /* 列表Dialog
     */
    public static void showListDialog(Context context) {
        final String[] items = { "我是1","我是2","我是3","我是4" };
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(context);
        listDialog.setTitle("我是一个列表Dialog");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        listDialog.show();
    }

    /* 单选Dialog
     */
    static int yourChoice;
    public static void showSingleChoiceDialog(Context context){
        final String[] items = { "我是1","我是2","我是3","我是4" };
        yourChoice = -1;
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(context);
        singleChoiceDialog.setTitle("我是一个单选Dialog");
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yourChoice = which;
                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (yourChoice != -1) {
                        }
                    }
                });
        singleChoiceDialog.show();
    }

    /* 多选Dialog
     */
    static ArrayList<Integer> yourChoices = new ArrayList<>();
    public static void showMultiChoiceDialog(Context context) {
        final String[] items = { "我是1","我是2","我是3","我是4" };
        // 设置默认选中的选项，全为false默认均未选中
        final boolean initChoiceSets[]={false,false,false,false};
        yourChoices.clear();
        AlertDialog.Builder multiChoiceDialog =
                new AlertDialog.Builder(context);
        multiChoiceDialog.setTitle("我是一个多选Dialog");
        multiChoiceDialog.setMultiChoiceItems(items, initChoiceSets,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        if (isChecked) {
                            yourChoices.add(which);
                        } else {
                            yourChoices.remove(which);
                        }
                    }
                });
        multiChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int size = yourChoices.size();
                        String str = "";
                        for (int i = 0; i < size; i++) {
                            str += items[yourChoices.get(i)] + " ";
                        }
                    }
                });
        multiChoiceDialog.show();
    }

    /* 等待Dialog
     */
    public static void showWaitingDialog(Context context) {
    /* 等待Dialog具有屏蔽其他控件的交互能力
     * @setCancelable 为使屏幕不可点击，设置为不可取消(false)
     * 下载等事件完成后，主动调用函数关闭该Dialog
     */
        ProgressDialog waitingDialog=
                new ProgressDialog(context);
        waitingDialog.setTitle("我是一个等待Dialog");
        waitingDialog.setMessage("等待中...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }

    /* 进度条Dialog
     */
    public static void showProgressDialog(Context context) {
    /* @setProgress 设置初始进度
     * @setProgressStyle 设置样式（水平进度条）
     * @setMax 设置进度最大值
     */
        final int MAX_PROGRESS = 100;
        final ProgressDialog progressDialog =
                new ProgressDialog(context);
        progressDialog.setProgress(0);
        progressDialog.setTitle("我是一个进度条Dialog");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(MAX_PROGRESS);
        progressDialog.show();
    /* 模拟进度增加的过程
     * 新开一个线程，每个100ms，进度增加1
     */
        new Thread(new Runnable() {
            @Override
            public void run() {
                int progress= 0;
                while (progress < MAX_PROGRESS){
                    try {
                        Thread.sleep(100);
                        progress++;
                        progressDialog.setProgress(progress);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                // 进度达到最大值后，窗口消失
                progressDialog.cancel();
            }
        }).start();
    }

    /* 编辑Dialog
     */
    public static void showInputDialog(Context context) {
    /*@setView 装入一个EditView
     */
        final EditText editText = new EditText(context);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(context);
        inputDialog.setTitle("我是一个输入Dialog").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                }).show();
    }

    /* 自定义Dialog
     */
    public static void showCustomizeDialog(Context context) {
    /* @setView 装入自定义View ==> R.layout.dialog_customize
     * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
     * dialog_customize.xml可自定义更复杂的View
     */
        AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(context);
        final View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_customize,null);
        customizeDialog.setTitle("我是一个自定义Dialog");
        customizeDialog.setView(dialogView);
        customizeDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 获取EditView中的输入内容
                        EditText edit_text =
                                (EditText) dialogView.findViewById(R.id.edit_text);
                    }
                });
        customizeDialog.show();
    }

    /* 复写回调函数
    复写Builder的create和show函数，可以在Dialog显示前实现必要设置
    例如初始化列表、默认选项等
    @create 第一次创建时调用
    @show 每次显示时调用
     */
    public static void showListCallbackDialog(Context context) {
        final String[] items = { "我是1","我是2","我是3","我是4" };
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(context){

                    @Override
                    public AlertDialog create() {
                        items[0] = "我是No.1";
                        return super.create();
                    }

                    @Override
                    public AlertDialog show() {
                        items[1] = "我是No.2";
                        return super.show();
                    }
                };
        listDialog.setTitle("我是一个列表Dialog");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // ...To-do
            }
        });
    /* @setOnDismissListener Dialog销毁时调用
     * @setOnCancelListener Dialog关闭时调用
     */
        listDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
            }
        });
        listDialog.show();
    }
}
