/*
 *
 * Copyright (C) HeonJik, KIM
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 * 
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free
 * Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */


package kr.graha.post.model.utility;

import kr.graha.post.lib.Record;
import kr.graha.post.model.Jdbc;
import kr.graha.post.model.Jndi;
import kr.graha.post.interfaces.ConnectionFactoryImpl;

/**
 * Graha(그라하) ConnectionFactory 인터페이스

 * @author HeonJik, KIM
 * @version 0.5
 * @since 0.1
 */


public class GrahaConnectionFactoryImpl extends ConnectionFactoryImpl {
	public GrahaConnectionFactoryImpl(Jndi jndi) {
		super(jndi);
	}
	public GrahaConnectionFactoryImpl(Jdbc jdbc, Record params) {
		super(jdbc, params);
	}
}
