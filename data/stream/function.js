var fun1 = function (name) {
    print('Hi there from Javascript, ' + name);
    return "greetings from javascript";
};

var fun2 = function (object) {
    print("JS Class Definition: " + Object.prototype.toString.call(object));
};


var MyJavaClass = Java.type('java_08.Main02');
var result = MyJavaClass.fun1('John Doe');

var fun3 = function () {
    var result = MyJavaClass.fun1('John Doe');
    print('js:' + result);
    MyJavaClass.fun2(new Number(23));
    MyJavaClass.fun2(new Date());
    MyJavaClass.fun2(123);

};

function Person(firstName, lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.getFullName = function () {
        return this.firstName + " " + this.lastName;
    }
}


// namespace  for invokeMethod
var MethodBack = {
    jsForward: function (s) {
        var result = s + ' it works!';
        return 'js: ' + result;
    },

    javaBack: function (s) {
        var result = MyJavaClass.fun1(s);
        print('js:  ' + result);

        result = MyJavaClass.fun2(s);
        print('js:  ' + result);


        MyJavaClass.fun2(new Number(23));
        MyJavaClass.fun2(new Date());

        return s;
    },
    javaMirror: function () {
        MyJavaClass.fun3({
                foo: 'bar',
                bar: 'foo'
            }
        )
    },
    javaMirrorArray: function () {
        MyJavaClass.fun3({f: new Number(23), m: new Date()}
        )
    },

    javaPerson: function () {
        var person1 = new Person("Peter", "Parker");
        MyJavaClass.fun4(person1);
    }

};
