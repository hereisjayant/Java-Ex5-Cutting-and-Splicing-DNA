package dna;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class PublicTests {

    @Test
    public void test1_create_getMass() {
        DNA dna1 = new DNA("ATGCCAxCTATGGTAG");
        assertEquals(2078.8, dna1.totalMass(), 0.001);
    }

    @Test
    public void test2_create_checkProtein() {
        DNA dna2 = new DNA("ATGCCAACATGGATGCCCGATAT++GGATTG+A!");
        assertTrue(dna2.isProtein());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test3_create_invalidSeq() {
        DNA dna3 = new DNA("-TA");
    }

    @Test
    public void test4_nucleotideCount() {
        DNA dna4 = new DNA("AAAGGTTACTGA");
        assertEquals(5, dna4.nucleotideCount('A'));
    }

    @Test
    public void test5_codonSet() {
        DNA dna5 = new DNA("AAAGGTTACTGA");
        HashSet<String> expectedSet = new HashSet<>();
        expectedSet.add("AAA");
        expectedSet.add("GGT");
        expectedSet.add("TAC");
        expectedSet.add("TGA");
        Set<String> cset = dna5.codonSet();
        cset.clear();
        assertEquals(expectedSet, dna5.codonSet());
    }

    @Test
    public void test6_getSequence() {
        DNA dna6 = new DNA("AAAGGTTACTGA");
        assertEquals("AAAGGTTACTGA", dna6.sequence());
    }

    @Test
    public void test7_mutate() {
        DNA dna7 = new DNA("AAAGGTTACTG+A");
        dna7.mutateCodon("TGA", "GAT");
        assertEquals("AAAGGTTACGAT", dna7.sequence());
    }

    @Test
    public void test8_mutate() {
        DNA dna8 = new DNA("AAAGGTTACTG+A");
        dna8.mutateCodon("TGA", "G+T");
        assertEquals("AAAGGTTACTG+A", dna8.sequence());
    }

    @Test
    public void test9_mutate() {
        DNA dna9 = new DNA("ATGCCAxCTATGGTAG");
        dna9.mutateCodon("CTA", "ATC");
        assertEquals(1978.8, dna9.totalMass(), 0.001);
        assertEquals("ATGCCAATCTGGTAG", dna9.sequence());
    }

    @Test
    public void test10_massHysteria() {
        DNA dna10_1 = new DNA("ATCGGG");
        DNA dna10_2 = new DNA("CCATGAAT*G");
        assertEquals(824.7, dna10_1.totalMass(), 0.001);
        assertEquals(1280.1, dna10_2.totalMass(), 0.001);
    }

    @Test
    public void test11_massATCGGG() {
        DNA dna10_1 = new DNA("ATCGGG");
        assertEquals(824.7, dna10_1.totalMass(), 0.001);
    }

    @Test
    public void test12_cutAndSplice() {
        DNA dna12 = new DNA("ATCGGGCATGTA");
        DNA expected = new DNA("ATCGGATTGATGCATGTA");
        DNA actual = dna12.cutAndSplice("GGGCAT", 2, "ATTGAT");
        assertEquals(expected, actual);
    }

    @Test
    public void test13_cutAndSplice() {
        DNA dna13 = new DNA("ATCGGGCATGTAGGGCAT");
        DNA expected = new DNA("ATCGGATTGATGCATGTAGGATTGATGCAT");
        DNA actual = dna13.cutAndSplice("GGGCAT", 2, "ATTGAT");
        assertEquals(expected, actual);
    }
}
