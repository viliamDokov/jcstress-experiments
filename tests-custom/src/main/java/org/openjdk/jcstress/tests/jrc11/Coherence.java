/*
 * Copyright (c) 2014, 2025, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.openjdk.jcstress.tests.varhandles;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.*;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.FORBIDDEN;

public class Coherence {

    private static class Holder {
        int a = 0;
        int trap = 0;
    }


    @JCStressTest @Description("Tests if Coherence ordering of JAM21 is valid")
    @Outcome(id = "2, 0", expect = Expect.ACCEPTABLE_INTERESTING, desc = "Release sequence missing")
    @Outcome(id = "1, 0", expect = FORBIDDEN, desc = "Visibility order")
    @Outcome(             expect = Expect.ACCEPTABLE, desc = "Boring")
    @State
    public static class ReleaseSequence {

        int x,y = 0;
        static final VarHandle VH_X, VH_Y;
        static {
            try {
                VH_X = MethodHandles.lookup().findVarHandle(ReleaseSequence.class, "x", int.class);
                VH_Y = MethodHandles.lookup().findVarHandle(ReleaseSequence.class, "y", int.class);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        @Actor
        public void actor1() {
            VH_X.set(this, 1);
            VH_Y.setRelease(this, 1);
            VH_Y.set(this, 2);
        }

        @Actor
        public void actor2(II_Result r) {
            r.r1 = (int) VH_Y.getAcquire(this);
            r.r2 = (int) VH_X.get(this);
        }
    }

    @JCStressTest
    @Description("Tests if Coherence ordering of JAM21 is valid")
    @Outcome(id = "1, 2, 1", expect = Expect.ACCEPTABLE_INTERESTING, desc = "JAM21 forbidden but rc11 allowed")
    @Outcome(id = "1, 1, 2", expect = Expect.ACCEPTABLE_INTERESTING, desc = "JAM21 ok and rc11 ok")
    @Outcome(             expect = Expect.ACCEPTABLE, desc = "Boring")
    @State
    public static class RelRelFun {
        static final VarHandle VH_X, VH_Y;
        static {
            try {
                VH_X = MethodHandles.lookup().findVarHandle(RelRelFun.class, "x", int.class);
                VH_Y = MethodHandles.lookup().findVarHandle(RelRelFun.class, "y", int.class);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        int x, y;

        @Actor
        public void actor1() {
            VH_X.set(this, 1);
            VH_Y.setRelease(this, 1);
        }

        @Actor
        public void actor2(III_Result r) {
            r.r1 = (int) VH_Y.get(this);
            VH_X.setRelease(this, 2);
        }

        @Actor
        public void actor3(III_Result r) {
            r.r2 = (int) VH_X.getOpaque(this);
            r.r3 = (int) VH_X.getOpaque(this);
        }
    }

    @JCStressTest
    @Description("SB_RFIS")
    @Outcome(id = "1, 0, 1, 0", expect = Expect.FORBIDDEN, desc = "Interesting outcome, that is forbidden by strong JMM interpretation (total order on volatiles)")
    @Outcome(             expect = Expect.ACCEPTABLE, desc = "Boring")
    @State
    public static class SbRfis {

        static final VarHandle VH_X, VH_Y, VH_FLAG;
        static {
            try {
                VH_X = MethodHandles.lookup().findVarHandle(SbRfis.class, "x", int.class);
                VH_Y = MethodHandles.lookup().findVarHandle(SbRfis.class, "y", int.class);
                VH_FLAG = MethodHandles.lookup().findVarHandle(SbRfis.class, "flag", int.class);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        int x, y, flag;


        @Actor
        public void actor1(IIII_Result r) {
            VH_X.set(this, 1);
            r.r1 = (int) VH_X.getVolatile(this);
            r.r2 = (int) VH_Y.getVolatile(this);
        }

        @Actor
        public void actor2(IIII_Result r) {
            VH_Y.set(this, 1);
            r.r3 = (int) VH_Y.getVolatile(this);
            r.r4 = (int) VH_X.getVolatile(this);
        }
    }

    @JCStressTest
    @Description("QuasiFence")
    @Outcome(id = "0, 0", expect = Expect.FORBIDDEN, desc = "Interesting outcome again that is forbidden by JAM21")
    @Outcome(             expect = Expect.ACCEPTABLE, desc = "Boring")
    @State
    public static class QuasiFence {

        static final VarHandle VH_X, VH_Y, VH_FLAG;
        static {
            try {
                VH_X = MethodHandles.lookup().findVarHandle(QuasiFence.class, "x", int.class);
                VH_Y = MethodHandles.lookup().findVarHandle(QuasiFence.class, "y", int.class);
                VH_FLAG = MethodHandles.lookup().findVarHandle(QuasiFence.class, "flag", int.class);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        int x, y, flag;


        @Actor
        public void actor1(II_Result r) {
            VH_X.set(this, 1);
            VH_FLAG.setVolatile(this, 42);
            VH_FLAG.getVolatile(this);
            r.r1 = (int) VH_Y.get(this);
        }

        @Actor
        public void actor2(II_Result r) {
            VH_Y.set(this, 1);
            VH_FLAG.setVolatile(this, 42);
            VH_FLAG.getVolatile(this);
            r.r2 = (int) VH_X.get(this);
        }
    }

    @JCStressTest
    @Description("QuasiFenceNoVH")
    @Outcome(id = "0, 0", expect = Expect.FORBIDDEN, desc = "Interesting outcome, forbidden by JAM21")
    @Outcome(             expect = Expect.ACCEPTABLE, desc = "Boring")
    @State
    public static class QuasiFenceNoVH {

        int x, y;
        volatile int flag;

        @Actor
        public void actor1(II_Result r) {
            x = 1;
            flag = 42; 
            int unused = flag;
            r.r1 = y;
        }
        @Actor
        public void actor2(II_Result r) {
            y = 1;
            flag = 42;
            int unused = flag;
            r.r2 = x;
        }
    }

    @JCStressTest
    @Description("QuasiFenceNoVH4Results")
    @Outcome(id = "42, 42, 0, 0", expect = Expect.FORBIDDEN, desc = "Interesting outcome fobidden by jam21")
    @Outcome(             expect = Expect.ACCEPTABLE, desc = "Boring")
    @State
    public static class QuasiFenceNoVH4Results {

        int x, y;
        volatile int flag;


        @Actor
        public void actor1(IIII_Result r) {
            x = 1;
            flag = 42;
            r.r1 = flag;
            r.r3 = y;
        }
        @Actor
        public void actor2(IIII_Result r) {
            y = 1;
            flag = 42;
            r.r2 = flag;
            r.r4 = x;
        }
    }
}
