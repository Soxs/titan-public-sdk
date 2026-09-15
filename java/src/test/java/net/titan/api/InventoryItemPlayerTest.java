package net.titan.api;

import com.google.inject.Guice;
import com.google.inject.AbstractModule;
import net.titan.api.internal.TitanRuntime;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InventoryItemPlayerTest {
 @Test void playerTargetPairPreservesIdentityAndSource() throws Exception {
  var observed=new AtomicReference<Object[]>();
  Client client=(Client)Proxy.newProxyInstance(Client.class.getClassLoader(),new Class<?>[]{Client.class},(p,m,a)->{
   if(m.getName().equals("invokeSelectedMenuAction")){observed.set(a);return true;}return m.getReturnType()==boolean.class?false:null;
  });
  var injector=Guice.createInjector(new AbstractModule(){protected void configure(){bind(Client.class).toInstance(client);}});
  TitanRuntime.setInjector(injector);
  try {
   InventoryItem source=new InventoryItem();set(source,"slot",4);set(source,"id",559);
   Player target=new Player();set(target,"hashIndex",42);set(target,"plane",2);set(target,"worldViewId",7);set(target,"entityPtr",0x123456789abcdef0L);
   assertTrue(source.useOn(target));var a=observed.get();assertNotNull(a);
   var selection=(MenuActionRequest)a[0];var action=(MenuActionRequest)a[1];
   assertEquals(25,selection.opcode());assertEquals(4,selection.param0());assertEquals(net.titan.gamevals.InterfaceID.Inventory.ITEMS,selection.param1());
   assertEquals(14,action.opcode());assertEquals(42,action.identifier());assertEquals(7,action.worldViewId());assertEquals(2,action.targetPlane());assertEquals(0x123456789abcdef0L,action.targetEntityPtr());assertEquals(559,a[2]);
   observed.set(null);assertFalse(source.useOn((Player)null));set(target,"hashIndex",-1);assertFalse(source.useOn(target));assertNull(observed.get());
  }finally{TitanRuntime.clearInjector(injector);}
 }
 static void set(Object target,String name,Object value)throws Exception{var field=target.getClass().getDeclaredField(name);field.setAccessible(true);field.set(target,value);}
}
