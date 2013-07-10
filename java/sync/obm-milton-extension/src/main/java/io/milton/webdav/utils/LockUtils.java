/*
 * Copyright 2013 McEvoy Software Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.milton.webdav.utils;

import io.milton.common.LogUtils;
import io.milton.common.Utils;
import io.milton.http.LockInfo;
import io.milton.http.LockResult;
import io.milton.http.LockToken;
import io.milton.http.Request;
import io.milton.http.Response;
import io.milton.http.XmlWriter;
import io.milton.http.entity.ByteArrayEntity;
import io.milton.http.values.CData;
import io.milton.http.webdav.DefaultPropFindPropertyBuilder;
import io.milton.http.webdav.WebDavProtocol;
import io.milton.resource.ICalResource;
import io.milton.resource.PropFindableResource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brad
 */
public final class LockUtils {

    private static final Logger log = LoggerFactory.getLogger(LockUtils.class);
    private static final Logger logLicense = LoggerFactory.getLogger("Milton.io");

    static {
        displayCopyrightNotice();
    }
    private static String D = WebDavProtocol.DAV_PREFIX;
    private static boolean stripHrefOnOwner = true;

    public static void init() {
        // do nothing
    }
    
    public static void add(List<String> list, String s) {
        if (!list.contains(s)) {
            list.add(s);
        }
    }

    public static void appendDepth(XmlWriter writer, LockInfo.LockDepth depthType) {
        String s = "Infinity";
        if (depthType != null) {
            if (depthType.equals(LockInfo.LockDepth.INFINITY)) {
                s = depthType.name().toUpperCase();
            }
        }
        writer.writeProperty(null, D + ":depth", s);

    }

    public static void appendOwner(XmlWriter writer, String owner) {
        boolean validHref;
        if (owner == null) {
            log.warn("owner is null");
            validHref = false;
        } else {
            validHref = isValidHref(owner);
        }
        log.debug("appendOwner: " + validHref + " - " + stripHrefOnOwner);
        if (!validHref && stripHrefOnOwner) { // BM: reversed login on validHref - presumably only write href tag for href values???
            writer.writeProperty(null, D + ":owner", owner);
        } else {
            XmlWriter.Element el = writer.begin(D + ":owner").open();
            XmlWriter.Element el2 = writer.begin(D + ":href").open();
            if (owner != null) {
                el2.writeText(owner);
            }
            el2.close();
            el.close();
        }
    }

    public static void appendScope(XmlWriter writer, LockInfo.LockScope scope) {
        writer.writeProperty(null, D + ":lockscope", "<" + D + ":" + scope.toString().toLowerCase() + "/>");
    }

    /**
     * Sets the timeout in seconds, with a maximum as required by the spec. See
     * http://jira.ettrema.com:8080/browse/MIL-89
     *
     * RFC4918 (14.29 timeout XML Element; 10.7 Timeout Request Header) states:
     * "The timeout value for TimeType "Second" MUST NOT be greater than 232-1."
     * 2^32 - 1 = 4 294 967 295 (136 years)
     * http://greenbytes.de/tech/webdav/rfc4918.html#HEADER_Timeout
     *
     * @param writer
     * @param seconds
     */
    public static void appendTimeout(XmlWriter writer, Long seconds) {
        if (seconds != null && seconds > 0) {
            writer.writeProperty(null, D + ":timeout", "Second-" + Utils.withMax(seconds, 4294967295l));
        }
    }

    public static void appendTokenId(XmlWriter writer, String tokenId) {
        XmlWriter.Element el = writer.begin(D + ":locktoken").open();
        writer.writeProperty(null, D + ":href", "opaquelocktoken:" + tokenId);
        el.close();
    }

    public static void appendType(XmlWriter writer, LockInfo.LockType type) {
        writer.writeProperty(null, D + ":locktype", "<" + D + ":" + type.toString().toLowerCase() + "/>");
    }

    public static void appendRoot(XmlWriter writer, String lockRoot) {
        XmlWriter.Element el = writer.begin(D + ":lockroot").open();
        writer.writeProperty(null, D + ":href", lockRoot);
        el.close();
    }

    /**
     * If set the owner value will not be wrapped in an href tag unless it is a
     * valid URL. E.g. true: this -> <owner>this</owner> false: that ->
     * <owner><href>that</href></owner>
     *
     * See also LockTokenValueWriter.java
     *
     * @return
     */
    public static boolean isStripHrefOnOwner() {
        return stripHrefOnOwner;
    }

    public static void setStripHrefOnOwner(boolean b) {
        stripHrefOnOwner = b;
    }

    private static boolean isValidHref(String owner) {
        if (owner.startsWith("http")) {
            try {
                URI u = new URI(owner);
                log.debug("uri: " + u);
                return true;
            } catch (URISyntaxException ex) {
                log.debug("ex: " + ex);
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Display information about licensing. Implemented here because this is one
     * of the few classes in milton which is generally not replaceable.
     */
    public static void displayCopyrightNotice() {
        System.out.println("");
        logLicense.info("Initializing Milton2 Webdav library Enterprise edition. Copyright McEvoy Software Limited");

        logLicense.info("Milton2 license found:");
        logLicense.info("Licensed for use with OBM under the terms of the Affero GPL");
    }

    public static byte[] readNormalisedLineEndings(InputStream in) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        IOUtils.copy(in, bout);
        String orig = bout.toString();
        String norm = orig.trim();
        norm = norm.replaceAll("\\r\\n", "\n");
        norm += "\n";
        byte[] licenseBytes = norm.getBytes("UTF-8");
        return licenseBytes;
    }


    public static void respondLocked(LockToken tok, Request request, Response response) {
        response.setStatus(Response.Status.SC_OK);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XmlWriter writer = new XmlWriter(out);
        writer.writeXMLHeader();
        String d = WebDavProtocol.DAV_PREFIX;
        writer.open(d + ":prop  xmlns:" + d + "=\"DAV:\"");
        writer.newLine();
        writer.open(d + ":lockdiscovery");
        writer.newLine();
        writer.open(d + ":activelock");
        writer.newLine();
        LockUtils.appendType(writer, tok.info.type);
        LockUtils.appendScope(writer, tok.info.scope);
        LockUtils.appendDepth(writer, tok.info.depth);
        LockUtils.appendOwner(writer, tok.info.lockedByUser);
        LockUtils.appendTimeout(writer, tok.timeout.getSeconds());
        LockUtils.appendTokenId(writer, tok.tokenId);
        String url = DefaultPropFindPropertyBuilder.fixUrlForWindows(request.getAbsoluteUrl());
        LockUtils.appendRoot(writer, url);
        writer.close(d + ":activelock");
        writer.close(d + ":lockdiscovery");
        writer.close(d + ":prop");
        writer.flush();

        LogUtils.debug(log, "lock response: ", out);
        response.setEntity(new ByteArrayEntity(out.toByteArray()));
//        response.close();

    }

    public static String parse(String ifHeader) {
        String token = ifHeader;
        int pos = token.indexOf(":");
        if (pos >= 0) {
            token = token.substring(pos + 1);
            pos = token.indexOf(">");
            if (pos >= 0) {
                token = token.substring(0, pos);
            }
        }
        return token;
    }

    public static void respondLockFailure(LockResult result, Request request, Response response) {
        log.info("respondWithLockFailure: " + result.getFailureReason().name());
        response.setStatus(result.getFailureReason().status);
    }

    public static CData getCalendarValue(PropFindableResource res) {
        if (res instanceof ICalResource) {
            ICalResource ical = (ICalResource) res;
            return new CData(ical.getICalData());
        } else {
            return null;
        }
    }
}
