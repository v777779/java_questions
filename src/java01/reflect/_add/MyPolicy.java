package java01.reflect._add;

import java.security.*;

class MyPolicy extends Policy {
    @Override
    public PermissionCollection getPermissions(CodeSource codesource) {
        Permissions p = new Permissions();
// all
        p.add(new AllPermission());
// same for A1.class
//        p.add(new ReflectPermission("suppressAccessChecks"));
//        p.add(new RuntimePermission("createSecurityManager"));
//        p.add(new RuntimePermission("setSecurityManager"));
//        p.add(new SecurityPermission("setPolicy"));

        return p;
    }

//        p.add(new PropertyPermission("java.class.path", "read"));
//        p.add(new FilePermission("/home/.../classes/*", "read"));
//        p.add(new FilePermission(A1.class.getResource(".").getPath()+"/*","read"));
//        p.add(new ReflectPermission(A1.class.getPackage().getName()));

}