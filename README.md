Kratos
=============

![Logo](images/logo.jpg)

Provide basic __Double Binding(Data Binding)__ feature on Android.

* Using annotation to generate boilerplate code.
* Bind view and data to help you clean your code.


 Example
----------------
The following code demostrate that two views(EditText and TextView) bound to one single data(which in the code `boundData` holds the data. you can later access or change the data by using `boundDate.data`)

```java
public class SimpleActivity extends Activity {

    @BindText({R.id.test_doublebinding_input, R.id.test_doublebinding_presenter})
    KString boundData = new KString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        Kratos.bind(this);
    }
}
```
The presenter(TextView) will behave exactly the same as input(EditText) since they were bound to the same data:

![Example](images/example.gif)

For more code see kratos-sample.


Download
-----------------
__Kratos is still under development and a lot of features haven't been added to it yet.__ But the basic idea is here. If you are interested in this project, feel free to fork.

Kratos is currently not available from maven central. 

How It Works
----------------
* Use Kotlin's [Observable Delegate][1] to listen to changes made to certian property.
* Use Kotlin's [Extension][2] feature to add functions to View.
* Use Annotation Processor to generate code which binds the data and the view(or views).


Lisence
----------------
[GNU GENERAL PUBLIC LICENSE Version 3][3]

[1]: https://kotlinlang.org/docs/reference/delegated-properties.html#observable
[2]: https://kotlinlang.org/docs/reference/extensions.html
[3]: http://www.gnu.org/licenses/gpl-3.0.en.html