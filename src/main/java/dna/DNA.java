package dna;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DNA {

    private static final Map<Character, Double> NUCLEOTIDE_MASS;
    private static final Set<String> PROTEIN_START_CODONS;
    private static final Set<String> PROTEIN_END_CODONS;
    private static final Set<Character> PROTEIN_NUCLEOTIDES;
    private static final double PROTEIN_N_MASS_THRESHOLD = 0.3;
    private static final int CODON_LENGTH = 3;
    private static final double JUNK_MASS = 100.000;
    private static double mass;

    /*
    Where we do some initialization for the entire class,
    and hide away constants.
     */
    static {
        NUCLEOTIDE_MASS = new HashMap<>();
        NUCLEOTIDE_MASS.put('A', 135.128);
        NUCLEOTIDE_MASS.put('C', 111.103);
        NUCLEOTIDE_MASS.put('G', 151.128);
        NUCLEOTIDE_MASS.put('T', 125.107);

        PROTEIN_START_CODONS = new HashSet<>();
        PROTEIN_START_CODONS.add("ATG");

        PROTEIN_END_CODONS = new HashSet<>();
        PROTEIN_END_CODONS.add("TAA");
        PROTEIN_END_CODONS.add("TAG");
        PROTEIN_END_CODONS.add("TGA");

        PROTEIN_NUCLEOTIDES = new HashSet<>();
        PROTEIN_NUCLEOTIDES.add('C');
        PROTEIN_NUCLEOTIDES.add('G');
    }

    private String sequence;
    private Set<String> codons;
    private Map<Character, Double> nucleotideMass = new HashMap<>();
    private boolean protein;
    private Map<Character, Integer> nucleotideCountMap = new HashMap<>();

    /**
     * Create new DNA given a nucleotide sequence.
     *
     * @param dnaSequence
     * @throws IllegalArgumentException if <code>dnaSequence</code> is invalid.
     *                                  A sequence is invalid if it contains nothing or an incomplete codon.
     */
    public DNA(String dnaSequence) throws IllegalArgumentException {

        nucleotideCountMap.put('A', 0);
        nucleotideCountMap.put('C', 0);
        nucleotideCountMap.put('G', 0);
        nucleotideCountMap.put('T', 0);

        sequence = dnaSequence;
        init();
    }

    /**
     * Analyzes and initializes useful data about the DNA sequence.
     * Requires that sequence be assigned before being invoked.
     *
     * @throws IllegalArgumentException when the sequence is invalid.
     */
    private void init() throws IllegalArgumentException {
        /*
        This is a single method where we perform some useful
        initialization operations.
        1. Compute the Codon Set
        2. Check if the DNA sequence represents a protein
        3. Compute the total mass of the sequence
         */

        codons = computeCodonSet();

        mass = computeMass();

        protein = proteinCheck();
    }

    /*
    Compute the mass of the DNA sequence
     */
    private double computeMass() {
        double totalMass = 0.0;
        for (Character c : sequence.toCharArray()) {
            if (NUCLEOTIDE_MASS.containsKey(c)) {
                totalMass += NUCLEOTIDE_MASS.get(c);
                if (nucleotideMass.containsKey(c)) {
                    nucleotideMass.put(c, nucleotideMass.get(c) + NUCLEOTIDE_MASS.get(c));
                } else {
                    nucleotideMass.put(c, NUCLEOTIDE_MASS.get(c));
                }
            } else {
                totalMass += DNA.JUNK_MASS;
            }
        }
        totalMass = Math.round(totalMass * 10.0) / 10.0; // and round to one decimal place
        return totalMass;
    }

    /*
    Compute the codon set for the sequence. If a codon is incomplete, or
    there is nothing in the sequence then throw an IllegalArgumentException.
     */
    private Set<String> computeCodonSet() throws IllegalArgumentException {
        if (sequence.equals("") || sequence == null) {
            throw new IllegalArgumentException("Empty or null sequence");
        }

        Set<String> codonSet = new HashSet<>();

        StringBuilder currentCodon = new StringBuilder();
        for (char c : sequence.toCharArray()) {
            if (NUCLEOTIDE_MASS.containsKey(c)) {
                nucleotideCountMap.put(c, nucleotideCountMap.get(c) + 1);
                currentCodon.append(c);
                if (currentCodon.length() == DNA.CODON_LENGTH) {
                    codonSet.add(currentCodon.toString());
                    currentCodon = new StringBuilder();
                }
            }
        }

        if (currentCodon.length() > 0) {
            throw new IllegalArgumentException("Unfinished codon");
        }

        return codonSet;
    }

    /*
    Helper method that checks if the sequence is a protein
     */
    private boolean proteinCheck() {
        try {
            if (codons == null) {
                codons = computeCodonSet();
            }
        } catch (IllegalArgumentException e) {
            return false;
        }

        if (codons.size() < 5) {
            return false;
        }

        StringBuilder sb = new StringBuilder();
        for (char c : sequence.toCharArray()) {
            if (NUCLEOTIDE_MASS.containsKey(c)) {
                sb.append(c);
                if (sb.length() == DNA.CODON_LENGTH) {
                    if (!PROTEIN_START_CODONS.contains(sb.toString())) {
                        return false;
                    } else {
                        break;
                    }
                }
            }
        }

        sb = new StringBuilder();
        for (int i = sequence.length() - 1; i >= 0; i--) {
            if (NUCLEOTIDE_MASS.containsKey(sequence.charAt(i))) {
                sb.insert(0, (sequence.charAt(i)));
                if (sb.length() == DNA.CODON_LENGTH) {
                    if (!PROTEIN_END_CODONS.contains(sb.toString())) {
                        return false;
                    } else {
                        break;
                    }
                }
            }
        }

        double cgmass = 0.0;
        for (Character c : PROTEIN_NUCLEOTIDES) {
            if (nucleotideMass.get(c) != null) {
                cgmass += nucleotideMass.get(c);
            }
        }
        if (cgmass / mass < PROTEIN_N_MASS_THRESHOLD) {
            return false;
        }

        return true;
    }

    /**
     * Is the DNA sequence a protein or not?
     *
     * @returns true if the sequence represents a protein and false otherwise.
     */
    public boolean isProtein() {
        return protein;
    }

    /**
     * Retrieves the mass of the DNA sequence.
     *
     * @return the total mass of the DNA sequence.
     */
    public double totalMass() {
        return mass;
    }

    /**
     * Obtain a set with all the codons in the DNA sequence.
     *
     * @return a set with all the codons in the DNA sequence.
     */
    public Set<String> codonSet() {
        Set<String> returnSet = new HashSet<String>();
        returnSet.addAll(codons);
        return returnSet;
    }

    /**
     * Obtain a count of the occurrences of a specific nucleotide in the DNA sequence.
     *
     * @param c is a valid nucleotide (one of A, C, G, T).
     * @return the number of times <code>c</code> occurs in the DNA sequence.
     */
    public int nucleotideCount(char c) {
        return nucleotideCountMap.get(c);
    }

    /**
     * Obtain the raw DNA sequence.
     *
     * @return the DNA sequence as it is.
     */
    public String sequence() {
        return sequence;
    }

    /**
     * Check if a set of three nucleotides is a valid codon or not.
     *
     * @param codon
     * @returns True if <code>codon</code> is a valid codon and false otherwise.
     */
    private boolean validCodon(String codon) {
        if (codon.length() != DNA.CODON_LENGTH) {
            return false;
        }
        for (char c : codon.toCharArray()) {
            if (!NUCLEOTIDE_MASS.containsKey(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Replace a codon with a different codon in the DNA sequence, effectively
     * mutating the DNA sequence <strong>and</strong> eliminating junk in the sequence.
     *
     * @param oldCodon is the codon to replace. If this codon is invalid or
     *                 does not occur in the sequence then nothing is changed.
     * @param newCodon is the new codon that is replacing <code>oldCodon</code>.
     *                 If this codon is invalid then nothing is changed.
     */
    public void mutateCodon(String oldCodon, String newCodon) {
        if (oldCodon.equals(newCodon)) {
            return;
        }

        if (!validCodon(oldCodon) || !validCodon(newCodon)) {
            return;
        }

        StringBuilder newSeq = new StringBuilder();
        StringBuilder currentCodon = new StringBuilder();
        boolean mutated = false;
        for (char c : sequence.toCharArray()) {
            if (NUCLEOTIDE_MASS.containsKey(c)) {
                currentCodon.append(c);
                if (currentCodon.length() == DNA.CODON_LENGTH) {
                    if (currentCodon.toString().equals(oldCodon)) {
                        newSeq.append(newCodon);
                        mutated = true;
                    } else {
                        newSeq.append(currentCodon.toString());
                    }
                    currentCodon = new StringBuilder();
                }
            }
        }
        if (mutated) {
            sequence = newSeq.toString();
            init();
        }
    }


    /**
     * Checks if a String represents an enzyme.
     *
     * @param enzyme
     * @return true if the string is an enzyme and false otherwise.
     */
    private boolean validEnzyme(String enzyme) {
        if (enzyme == null) {
            return false;
        }

        if (enzyme.length() % DNA.CODON_LENGTH != 0) {
            return false;
        }

        for (char c : enzyme.toCharArray()) {
            if (!NUCLEOTIDE_MASS.containsKey(c)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Generate a new DNA sequence by cutting and splicing a DNA sequence.
     * This operation also removes all junk. If the restriction enzyme is not
     * found in the DNA sequence then the DNA sequence created is identical to
     * the original sequence without any junk.
     * <p>
     * See the exercise spec for details about the cut-and-splice operation.
     *
     * @param restrictionEnzyme is not null, not "", and is a sequence of codons with no junk.
     *                          This is the codon sequence that will be cleaved.
     * @param splicePosition    is the position within the restriction enzyme
     *                          where the splicee is added.
     *                          0 < splicePosition < length of restriction enzyme.
     * @param splicee           is not null, not "", and is a sequence of codons with no junk.
     *                          This is the enzyme to splice in to the DNA sequence
     *                          where the restriction enzyme is cleaved.
     * @return DNA object created by the cut-and-splice operation.
     * @throws IllegalArgumentException
     */
    public DNA cutAndSplice(String restrictionEnzyme, int splicePosition, String splicee) {

        if (!validEnzyme(restrictionEnzyme) || !validEnzyme(splicee)) {
            throw new IllegalArgumentException("Invalid enzymes");
        }

        if (splicePosition <= 0 || splicePosition >= restrictionEnzyme.length()) {
            throw new IllegalArgumentException("Invalid splice position");
        }

        StringBuilder newSeqBuilder = new StringBuilder();

        /*
        Let us eliminate the junk first
         */
        for (char c : sequence.toCharArray()) {
            if (NUCLEOTIDE_MASS.containsKey(c)) {
                newSeqBuilder.append(c);
            }
        }

        String seqNoJunk = newSeqBuilder.toString();

        newSeqBuilder = new StringBuilder();

        /*
        Now we try to match the enzyme in the sequence.
        If we find a match, we insert the splicee at the appropriate
        point.
         */
        int i = 0;
        while (i < seqNoJunk.length() - restrictionEnzyme.length()) {
            boolean enzymeMatch = false;
            for (int j = 0; j < restrictionEnzyme.length(); j++) {
                enzymeMatch = true;
                if (sequence.charAt(i + j) != restrictionEnzyme.charAt(j)) {
                    newSeqBuilder.append(seqNoJunk.substring(i, i + DNA.CODON_LENGTH));
                    i = i + DNA.CODON_LENGTH;
                    enzymeMatch = false;
                    break;
                }
            }
            if (enzymeMatch) {
                newSeqBuilder.append(seqNoJunk.substring(i, i + splicePosition));
                newSeqBuilder.append(splicee);
                newSeqBuilder.append(seqNoJunk.substring(i + splicePosition));
                i = i + restrictionEnzyme.length();
            }
        }

        return new DNA(newSeqBuilder.toString());

    }

    // Overriding equals() to compare two DNA objects
    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        if (!(o instanceof DNA)) {
            return false;
        }


        // Compare the data members and return accordingly
        return this.sequence().equals(((DNA) o).sequence);
    }
}
