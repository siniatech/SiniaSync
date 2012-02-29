package com.siniatech.siniasync.util;

import static com.siniatech.siniautils.test.AssertHelper.*;
import static junit.framework.Assert.*;

import java.lang.reflect.Constructor;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.siniatech.siniasync.change.FileContentsChange;
import com.siniatech.siniasync.change.FileMissingChange;
import com.siniatech.siniasync.change.FileTypeChange;
import com.siniatech.siniautils.fn.IFunction0;
import com.siniatech.siniautils.fn.IResponse1;
import com.siniatech.siniautils.fn.IResponse2;

public class TestHashCodeAndEquals {

    @SuppressWarnings("rawtypes")
    private static List<Class> classesToTest = Arrays.<Class> asList( //
        FileMissingChange.class, //
        FileTypeChange.class, //
        FileContentsChange.class //
        );

    @SuppressWarnings("rawtypes")
    private static Map<Class, IFunction0> mockClasses = new HashMap<Class, IFunction0>();

    static long seq = 0;

    static {
        mockClasses.put( Path.class, new IFunction0<Path>() {
            public Path apply() {
                return FileSystems.getDefault().getPath( "f" + seq++ );
            }
        } );
    }

    private void visitClasses( IResponse1<Object> response ) throws Exception {
        for ( Class<?> clazz : classesToTest ) {
            response.respond( instantiate( clazz ) );
        }
    }

    private void visitClassesWithDiffConstructorParams( IResponse2<Object, Object> response ) throws Exception {
        for ( Class<?> clazz : classesToTest ) {
            response.respond( instantiate( clazz ), instantiate( clazz ) );
        }
    }

    private void visitClassesWithNullConstructorParamsOneSide( IResponse2<Object, Object> response ) throws Exception {
        for ( Class<?> clazz : classesToTest ) {
            Object stdObj = instantiate( clazz );
            for ( Object objWithNulls : instantiateWithNulls( clazz ) ) {
                response.respond( objWithNulls, stdObj );
            }
        }
    }

    private void visitClassesWithNullConstructorParamsBothSides( IResponse2<Object, Object> response ) throws Exception {
        for ( Class<?> clazz : classesToTest ) {
            for ( Object objWithNulls : instantiateWithNulls( clazz ) ) {
                response.respond( objWithNulls, objWithNulls );
            }
        }
    }

    private List<Object> instantiateWithNulls( Class<?> clazz ) throws Exception {
        assert !mockClasses.containsKey( clazz );
        Constructor<?> constructor = clazz.getConstructors()[0];
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        List<Object> objs = new ArrayList<Object>();
        for ( int i = 0; i < parameterTypes.length; i++ ) {
            objs.add( constructor.newInstance( instantiateWithNullAt( parameterTypes, i ) ) );
        }
        return objs;
    }

    private Object[] instantiateWithNullAt( Class<?>[] parameterTypes, int nullAt ) throws Exception {
        Object[] parameters = new Object[parameterTypes.length];
        for ( int i = 0; i < parameterTypes.length; i++ ) {
            parameters[i] = i == nullAt ? null : instantiate( parameterTypes[i] );
        }
        return parameters;
    }

    private Object[] instantiate( Class<?>[] parameterTypes ) throws Exception {
        Object[] parameters = new Object[parameterTypes.length];
        for ( int i = 0; i < parameterTypes.length; i++ ) {
            parameters[i] = instantiate( parameterTypes[i] );
        }
        return parameters;
    }

    private Object instantiate( Class<?> clazz ) throws Exception {
        if ( mockClasses.containsKey( clazz ) ) {
            return mockClasses.get( clazz ).apply();
        } else {
            Constructor<?> constructor = clazz.getConstructors()[0];
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            return constructor.newInstance( instantiate( parameterTypes ) );
        }
    }

    @Test
    public void testEqualsNull() throws Exception {
        visitClasses( new IResponse1<Object>() {
            public void respond( Object t ) {
                assertFalse( t.equals( null ) );
            }
        } );
    }

    @Test
    public void testEqualsIdentity() throws Exception {
        visitClasses( new IResponse1<Object>() {
            public void respond( Object t ) {
                assertTrue( t.equals( t ) );
            }
        } );
    }

    @Test
    public void testEqualsOtherClass() throws Exception {
        visitClasses( new IResponse1<Object>() {
            public void respond( Object t ) {
                assertFalse( t.equals( 1 ) );
            }
        } );
    }

    @Test
    public void testEqualsWithNullParamsOneSide() throws Exception {
        visitClassesWithNullConstructorParamsOneSide( new IResponse2<Object, Object>() {
            public void respond( Object t, Object u ) {
                assertFalse( t.equals( u ) );
            }
        } );
    }

    @Test
    public void testEqualsWithDiffConstructorParams() throws Exception {
        visitClassesWithDiffConstructorParams( new IResponse2<Object, Object>() {
            public void respond( Object t, Object u ) {
                assertFalse( t.equals( u ) );
            }
        } );
    }

    @Test
    public void testEqualsWithNullParamsBothSides() throws Exception {
        visitClassesWithNullConstructorParamsBothSides( new IResponse2<Object, Object>() {
            public void respond( Object t, Object u ) {
                assertTrue( t.equals( u ) );
            }
        } );
    }

    @Test
    public void testHashCode() throws Exception {
        visitClasses( new IResponse1<Object>() {
            public void respond( Object t ) {
                assertNotEquals( 0, t.hashCode() );
                assertNotEquals( 1, t.hashCode() );
                assertEquals( t.hashCode(), t.hashCode() );
            }
        } );
    }

    @Test
    public void testHashCodeWithNullParamsOneSide() throws Exception {
        visitClassesWithNullConstructorParamsOneSide( new IResponse2<Object, Object>() {
            public void respond( Object t, Object u ) {
                assertNotEquals( t.hashCode(), u.hashCode() );
            }
        } );
    }

    @Test
    public void testHashCodeWithNullParamsBothSides() throws Exception {
        visitClassesWithNullConstructorParamsBothSides( new IResponse2<Object, Object>() {
            public void respond( Object t, Object u ) {
                assertEquals( t.hashCode(), u.hashCode() );
            }
        } );
    }
}
