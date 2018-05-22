var MyJavaClass = Java.type('java_08.Main02');
var result = MyJavaClass.fun1('John Doe');
print('js:  ' + result);

MyJavaClass.fun2(123);
MyJavaClass.fun2(49.99);
MyJavaClass.fun2(true);
MyJavaClass.fun2("hi there");
MyJavaClass.fun2(new Number(23));
MyJavaClass.fun2(new Date());
MyJavaClass.fun2(new RegExp());
MyJavaClass.fun2({foo: 'bar'});
