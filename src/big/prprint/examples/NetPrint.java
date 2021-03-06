/*
 * Copyright (C) 2015 EPresident <prez_enquiry@hotmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package big.prprint.examples;

import big.net.Utils;
import big.prprint.BigPPrinterVeryPretty;
import big.prprint.DotLangPrinter;
import it.uniud.mads.jlibbig.core.std.Bigraph;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class NetPrint {   
    
    public static void main(String[] args){
        Bigraph big = Utils.clientServerPacketExchange();
        BigPPrinterVeryPretty pprt = new BigPPrinterVeryPretty();
        DotLangPrinter dlp = new DotLangPrinter();
        System.out.println(pprt.prettyPrint(big));
        dlp.printDotFile(big, "Network", "net_example");
    }
}
