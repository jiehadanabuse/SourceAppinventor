// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.components.runtime;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.PropertyCategory;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.common.YaVersion;
import com.google.appinventor.components.runtime.util.AnimationUtil;
import com.google.appinventor.components.runtime.util.ErrorMessages;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

import java.io.File; 

/**
 * Implementation of a general Android Activity component.
 *
 * @author markf@google.com (Mark Friedman)
 */
@DesignerComponent(version = YaVersion.ACTIVITYSTARTER_COMPONENT_VERSION,
    designerHelpDescription = "<p>A component that can launch an activity " +
    "using the <code>StartActivity</code> method.</p>" +
    "<p>Activities that can be launched include:<ul> " +
    "<li> starting other App Inventor for Android apps </li> " +
    "<li> starting the camera application </li> " +
    "<li> performing web search </li> " +
    "<li> opening a browser to a specified web page</li> " +
    "<li> opening the map application to a specified location</li></ul> " +
    "You can also launch activities that return text data.  See the " +
    "documentation on using the Activity Starter for examples." +
    "</p>",

    // TODO(user): Add more information about bringing up maps when
    // the issues with html quoting (bug 2386151) are fixed.
    description = "<p>A component that can launch an activity using " +
    "the <code>StartActivity</code> method.</p>" +
    "<p>Activities that can be launched include:<ul> " +
    "<li> Starting another App Inventor for Android app.  To do so, first " +
    "     find out the <em>class</em> of the other application by " +
    "     downloading the source code and using a file explorer or unzip " +
    "     utility to find a file named " +
    "     \"youngandroidproject/project.properties\".  The first line of " +
    "     the file will start with \"main=\" and be followed by the class " +
    "     name; for example, " +
    "     <code>main=com.gmail.Bitdiddle.Ben.HelloPurr.Screen1</code>.  " +
    "     (The first components indicate that it was created by " +
    "     Ben.Bitdiddle@gmail.com.)  To make your " +
    "     <code>ActivityStarter</code> launch this application, set the " +
    "     following properties: <ul> " +
    "     <li> <code>ActivityPackage</code> to the class name, dropping the " +
    "          last component (for example, " +
    "          <code>com.gmail.Bitdiddle.Ben.HelloPurr</code>)</li> " +
    "     <li> <code>ActivityClass</code> to the entire class name (for " +
    "          example, " +
    "          <code>com.gmail.Bitdiddle.Ben.HelloPurr.Screen1</code>)</li> " +
    "     </ul></li>" +
    "<li> Starting the camera application by setting the following " +
    "     properties:<ul> " +
    "     <li> <code>Action: android.intent.action.MAIN</code> </li> " +
    "     <li> <code>ActivityPackage: com.android.camera</code> </li> " +
    "     <li> <code>ActivityClass: com.android.camera.Camera</code></li> " +
    "     </ul></li>" +
    "<li> Performing web search.  Assuming the term you want to search " +
    "     for is \"vampire\" (feel free to substitute your own choice), " +
    "     set the properties to:<blockquote><code> " +
    "     Action: android.intent.action.WEB_SEARCH<br/> " +
    "     ExtraKey: query<br/> " +
    "     ExtraValue: vampire<br/> " +
    "     ActivityPackage: com.google.android.providers.enhancedgooglesearch<br/>" +
    "     ActivityClass: com.google.android.providers.enhancedgooglesearch.Launcher<br/> " +
    "     </code></blockquote></li> " +
    "<li> Opening a browser to a specified web page.  Assuming the page you " +
    "     want to go to is \"www.facebook.com\" (feel free to substitute " +
    "     your own choice), set the properties to: <blockquote><code> " +
    "     Action: android.intent.action.VIEW <br/> " +
    "     DataUri: http://www.facebook.com </code> </blockquote> </li> " +
    "</ul>" +
    "</p>",
    category = ComponentCategory.MISC,
    nonVisible = true,
    iconName = "images/activityStarter.png")
@SimpleObject
public class ActivityStarter extends AndroidNonvisibleComponent
    implements ActivityResultListener, Component, Deleteable {

  private String action;
  private String dataUri;
  private String dataType;
  private String activityPackage;
  private String activityClass;
  private String extraKey;
  private String extraKey1;
  private String extraKey2;
  private String extraKey3;
  private String extraKey4;
  private String extraKey5;
  private String extraValue;
  private String extraValue1;
  private String extraValue2;
  private String extraValue3;
  private String extraValue4;
  private String extraValue5;
  private String resultName;
  private Intent resultIntent;
  private String result;
  private int requestCode;
  private final ComponentContainer container;

  /**
   * Creates a new ActivityStarter component.
   *
   * @param container  container, kept for access to form and context
   */
  public ActivityStarter(ComponentContainer container) {
    super(container.$form());
    // Save the container for later
    this.container = container;
    result = "";
    Action(Intent.ACTION_MAIN);
    ActivityPackage("");
    ActivityClass("");
    DataUri("");
    DataType("");
    ExtraKey("");
    ExtraKey1("");
    ExtraKey2("");
    ExtraKey3("");
    ExtraKey4("");
    ExtraKey5("");
    ExtraValue("");
    ExtraValue1("");
    ExtraValue2("");
    ExtraValue3("");
    ExtraValue4("");
    ExtraValue5("");
    ResultName("");
  }

  /**
   * Returns the action that will be used to start the activity.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String Action() {
    return action;
  }

  /**
   * Specifies the action that will be used to start the activity.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void Action(String action) {
    this.action = action.trim();
  }

  // TODO(lizlooney) - currently we support just one extra name/value pair that will be passed to
  // the activity. The user specifies the ExtraKey and ExtraValue properties.
  // We should allow more extra name/value pairs, but we'd need a different interface with regard
  // to properties and functions.
  // In the documentation for Intent, they use the term "name", not "key", and we might want to use
  // the term "name", also.
  // There are backwards compatibility issues with removing the ExtraKey and ExtraValue properties.
  // Also, while extra names are always Strings, the values can be other types. We'd need to know
  // the correct type of the value in order to call the appropriate Intent.putExtra method.
  // Adding multiple functions like PutStringExtra, PutStringArrayExtra, PutCharExtra,
  // PutCharArrayExtra, PutBooleanExtra, PutBooleanArrayExtra, PutByteExtra, PutByteArrayExtra,
  // PutShortExtra, PutShortArrayExtra, PutIntExtra, PutIntArrayExtra, PutLongExtra,
  // PutLongArrayExtra, PutFloatExtra, PutFloatArrayExtra, PutDoubleExtra, PutDoubleArrayExtra,
  // etc, seems like a bad idea.

  /**
   * Returns the extra key that will be passed to the activity.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ExtraKey() {
    return extraKey;
  }

  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ExtraKey1() {
    return extraKey1;
  }
  
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ExtraKey2() {
    return extraKey2;
  }

  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ExtraKey3() {
    return extraKey3;
  }

  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ExtraKey4() {
    return extraKey4;
  }

  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ExtraKey5() {
    return extraKey5;
  }
 
  /**
   * Specifies the extra key that will be passed to the activity.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ExtraKey(String extraKey) {
    this.extraKey = extraKey.trim();
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ExtraKey1(String extraKey1) {
    this.extraKey1 = extraKey1.trim();
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ExtraKey2(String extraKey2) {
    this.extraKey2 = extraKey2.trim();
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ExtraKey3(String extraKey3) {
    this.extraKey3 = extraKey3.trim();
  }  

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ExtraKey4(String extraKey4) {
    this.extraKey4 = extraKey4.trim();
  }
  
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ExtraKey5(String extraKey5) {
    this.extraKey5 = extraKey5.trim();
  }

  /**
   * Returns the extra value that will be passed to the activity.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ExtraValue() {
    return extraValue;
  }
  
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ExtraValue1() {
    return extraValue1;
  }

  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ExtraValue2() {
    return extraValue2;
  }

  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ExtraValue3() {
    return extraValue3;
  }

  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ExtraValue4() {
    return extraValue4;
  }

  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ExtraValue5() {
    return extraValue5;
  }



  /**
   * Specifies the extra value that will be passed to the activity.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ExtraValue(String extraValue) {
    this.extraValue = extraValue.trim();
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ExtraValue1(String extraValue1) {
    this.extraValue1 = extraValue1.trim();
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ExtraValue2(String extraValue2) {
    this.extraValue2 = extraValue2.trim();
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ExtraValue3(String extraValue3) {
    this.extraValue3 = extraValue3.trim();
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ExtraValue4(String extraValue4) {
    this.extraValue4 = extraValue4.trim();
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ExtraValue5(String extraValue5) {
    this.extraValue5 = extraValue5.trim();
  }




  // TODO(lizlooney) - currently we support retrieving just one string extra result from the
  // activity. The user specifies the ResultName property and, then after the activity finishes,
  // the string extra result corresponding to ResultName is passed as the result parameter to the
  // AfterActivity event and is also available from the Result property getter.
  // We should allow access to more extra results, but we'd need a different interface with regard
  // to properties, functions, and events parameters.
  // There are backwards compatibility issues with removing the AfterActivity event's result
  // parameter and the Result property.
  // Also, while extra names are always Strings, the values can be other types. We'd need to know
  // the correct type of the value in order to call the appropriate Intent.get...Extra method.
  // Adding multiple functions like GetStringExtra, GetStringArrayExtra, GetCharExtra,
  // GetCharArrayExtra, GetBooleanExtra, GetBooleanArrayExtra, GetByteExtra, GetByteArrayExtra,
  // GetShortExtra, GetShortArrayExtra, GetIntExtra, GetIntArrayExtra, GetLongExtra,
  // GetLongArrayExtra, GetFloatExtra, GetFloatArrayExtra, GetDoubleExtra, GetDoubleArrayExtra,
  // etc, seems like a bad idea.

  /**
   * Returns the name that will be used to retrieve a result from the activity.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ResultName() {
    return resultName;
  }

  /**
   * Specifies the name that will be used to retrieve a result from the
   * activity.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ResultName(String resultName) {
    this.resultName = resultName.trim();
  }

  /**
   * Returns the result from the activity.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String Result() {
    return result;
  }

  /**
   * Returns the data URI that will be used to start the activity.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String DataUri() {
    return dataUri;
  }

  /**
   * Specifies the data URI that will be used to start the activity.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void DataUri(String dataUri) {
    this.dataUri = dataUri.trim();
  }

  /**
   * Returns the MIME type to pass to the activity.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String DataType() {
    return dataType;
  }

  /**
   * Specifies the MIME type to pass to the activity.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void DataType(String dataType) {
    this.dataType = dataType.trim();
  }

  /**
   * Returns the package part of the specific component that will be started.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ActivityPackage() {
    return activityPackage;
  }

  /**
   * Specifies the package part of the specific component that will be started.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ActivityPackage(String activityPackage) {
    this.activityPackage = activityPackage.trim();
  }

  /**
   * Returns the class part of the specific component that will be started.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ActivityClass() {
    return activityClass;
  }

  /**
   * Specifies the class part of the specific component that will be started.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ActivityClass(String activityClass) {
    this.activityClass = activityClass.trim();
  }

  @SimpleEvent(description = "Event raised after this ActivityStarter returns.")
  public void AfterActivity(String result) {
    EventDispatcher.dispatchEvent(this, "AfterActivity", result);
  }

  /**
   * Returns the MIME type from the activity.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ResultType() {
    if (resultIntent != null) {
      String resultType = resultIntent.getType();
      if (resultType != null) {
        return resultType;
      }
    }
    return "";
  }

  /**
   * Returns the URI from the activity.
   */
  @SimpleProperty(
      category = PropertyCategory.BEHAVIOR)
  public String ResultUri() {
    if (resultIntent != null) {
      String resultUri = resultIntent.getDataString();
      if (resultUri != null) {
        return resultUri;
      }
    }
    return "";
  }


  /**
   * Returns the name of the activity that corresponds to this ActivityStarer,
   * or an empty string if no corresponding activity can be found.
   */
  @SimpleFunction(description = "Returns the name of the activity that corresponds to this " +
      "ActivityStarer, or an empty string if no corresponding activity can be found.")
  public String ResolveActivity() {
    Intent intent = buildActivityIntent();
    PackageManager pm = container.$context().getPackageManager();
    ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
    if (resolveInfo != null && resolveInfo.activityInfo != null) {
      return resolveInfo.activityInfo.name;
    }
    return "";
  }

  /**
   * Start the activity.
   */
  @SimpleFunction(description = "Start the activity corresponding to this ActivityStarter.")
  public void StartActivity() {
    resultIntent = null;
    result = "";

    Intent intent = buildActivityIntent();

    if (requestCode == 0) {
      // First time, we need to register this as an ActivityResultListener with the Form.
      // The Form's onActivityResult method will be called when the activity returns. If we
      // register with the Form and then use the requestCode when we start an activity, the Form
      // will call our resultReturned method.
      requestCode = form.registerForActivityResult(this);
    }

    try {
      container.$context().startActivityForResult(intent, requestCode);
      String openAnim = container.$form().getOpenAnimType();
      AnimationUtil.ApplyOpenScreenAnimation(container.$context(), openAnim);
    } catch (ActivityNotFoundException e) {
      form.dispatchErrorOccurredEvent(this, "StartActivity",
          ErrorMessages.ERROR_ACTIVITY_STARTER_NO_CORRESPONDING_ACTIVITY);
    }
  }

  private Intent buildActivityIntent() {
    Uri uri = (dataUri.length() != 0) ? Uri.parse(dataUri) : null;
    Intent intent = (uri != null) ? new Intent(action, uri) : new Intent(action);

    if (dataType.length() != 0) {
      if (uri != null) {
        intent.setDataAndType(uri, dataType);
      } else {
        intent.setType(dataType);
      }
    }

    if (activityPackage.length() != 0 || activityClass.length() != 0) {
      ComponentName component = new ComponentName(activityPackage, activityClass);
      intent.setComponent(component);
    }

    if (extraKey.length() != 0 && extraValue.length() != 0) {
      intent.putExtra(extraKey, extraValue);
    }
    
    if (extraKey1.length() != 0 && extraValue1.length() != 0) {
      intent.putExtra(extraKey1, extraValue1);
    }

    if (extraKey2.length() != 0 && extraValue2.length() != 0) {
      intent.putExtra(extraKey2, extraValue2);
    }

    if (extraKey3.length() != 0 && extraValue3.length() != 0) {
      intent.putExtra(extraKey3, extraValue3);
    }

    if (extraKey4.length() != 0 && extraValue4.length() != 0) {
      intent.putExtra(extraKey4, extraValue4);
    }

    if (extraKey5.length() != 0 && extraValue5.length() != 0) {
      intent.putExtra(extraKey5, extraValue5);
    }

    return intent;
  }

  @Override
  public void resultReturned(int requestCode, int resultCode, Intent data) {
    if (requestCode == this.requestCode) {
      Log.i("ActivityStarter", "resultReturned - resultCode = " + resultCode);
      if (resultCode == Activity.RESULT_OK) {
        resultIntent = data;
        if (resultName.length() != 0 && resultIntent != null &&
            resultIntent.hasExtra(resultName)) {
          result = resultIntent.getStringExtra(resultName);
        } else {
          result = "";
        }
        // call user's AfterActivity event handler
        AfterActivity(result);
      }
    }
  }

  @SimpleEvent(description = "The ActivityError event is no longer used. " +
      "Please use the Screen.ErrorOccurred event instead.",
      userVisible = false)
  public void ActivityError(String message) {
  }

  // Deleteable implementation

  @Override
  public void onDelete() {
    form.unregisterForActivityResult(this);
  }
  
  /**
   * Block ShareText:
   * Lauch activity for share text directly xcitizen.team@Gmail.com
   *
   */
   
  @SimpleFunction(description = "Start ShareText Type=text/html or text/plain, " +
  "Subject=Only for mail share, Text=Insert text for share")
  public void ShareText(String Type,String Subject,String Text) {
         final Intent intent = new Intent(Intent.ACTION_SEND);
 
         intent.setType(Type);
         intent.putExtra(Intent.EXTRA_SUBJECT, Subject);
         intent.putExtra(Intent.EXTRA_TEXT, Text);
         container.$context().startActivityForResult(intent, requestCode);
      String openAnim = container.$form().getOpenAnimType();
      AnimationUtil.ApplyOpenScreenAnimation(container.$context(), openAnim);
    }
    
  /**
   * Block SendEmail:
   * Lauch activity for send email with attached directly xcitizen.team@gmail.com
   */
   
    @SimpleFunction(description = "SendEmail with attached")
    public void SendEmail(String Type,String Subject,String Atach, String Text, String Email) {
             final Intent intent = new Intent(Intent.ACTION_SEND);
              File dir = new File("/mnt/sdcard/");
              File file = new File(dir.getAbsolutePath(), Atach);
              intent.putExtra(Intent.EXTRA_EMAIL, new String[] { Email });
              intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
              intent.putExtra(Intent.EXTRA_SUBJECT, Subject);
              intent.putExtra(Intent.EXTRA_TEXT, Text);
              intent.setType(Type);
              container.$context().startActivityForResult(intent, requestCode);
      String openAnim = container.$form().getOpenAnimType();
      AnimationUtil.ApplyOpenScreenAnimation(container.$context(), openAnim);
    }

    /**
   * Block ShareImage:
   * Lauch activity for share one image xcitizen.team@gmail.com
   */
    @SimpleFunction(description = "Start ShareImage Type=image/* or image/jpg or bmp or png," +
        "Path for image to share example file:///mnt/sdcard/test.jpg")
  public void ShareImage(String Type,String File) {
         final Intent intent = new Intent(Intent.ACTION_SEND);
         intent.setType(Type);
         intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(File));
         container.$context().startActivityForResult(intent, requestCode);
      String openAnim = container.$form().getOpenAnimType();
      AnimationUtil.ApplyOpenScreenAnimation(container.$context(), openAnim);
	}

   /**
   * Block ShowMap:
   * Lauch activity for showmap xcitizen.team@gmail.com
   */
   @SimpleFunction(description = "Examples Maps geo:Lat,Lon or geo:Lat,Lon?z=zoom or geo:0,0?q=direction+especific" +
   "or geo:0,0?q=deal+city or geo:0,0?q=Lat,Long(label+information) or https://maps.google.com/maps?saddr=Lat,Lon&daddr=Lat,Lon")
	public void ShowMap(String Map){
	   final Intent intent = new Intent(Intent.ACTION_VIEW);
	   intent.setData(Uri.parse(Map));
	   container.$context().startActivityForResult(intent, requestCode);
	      String openAnim = container.$form().getOpenAnimType();
	      AnimationUtil.ApplyOpenScreenAnimation(container.$context(), openAnim);          
	        }
      
}
