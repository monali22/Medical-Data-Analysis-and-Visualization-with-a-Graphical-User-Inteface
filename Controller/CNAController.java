package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.control.TextField;

import java.io.*;
import java.util.List;

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;
import org.rosuda.REngine.JRI.JRIEngine;
import org.rosuda.JRI.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import java.awt.*;


class TextConsole implements RMainLoopCallbacks
{
    public void rWriteConsole(Rengine re, String text, int oType) {
        System.out.print(text);
    }

    public void rBusy(Rengine re, int which) {
        System.out.println("rBusy("+which+")");
    }

    public String rReadConsole(Rengine re, String prompt, int addToHistory) {
        System.out.print(prompt);
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            String s=br.readLine();
            return (s==null||s.length()==0)?s:s+"\n";
        } catch (Exception e) {
            System.out.println("jriReadConsole exception: "+e.getMessage());
        }
        return null;
    }

    public void rShowMessage(Rengine re, String message) {
        System.out.println("rShowMessage \""+message+"\"");
    }

    public String rChooseFile(Rengine re, int newFile) {
        FileDialog fd = new FileDialog(new Frame(), (newFile==0)?"Select a file":"Select a new file", (newFile==0)?FileDialog.LOAD:FileDialog.SAVE);
        fd.show();
        String res=null;
        if (fd.getDirectory()!=null) res=fd.getDirectory();
        if (fd.getFile()!=null) res=(res==null)?fd.getFile():(res+fd.getFile());
        return res;
    }

    public void   rFlushConsole (Rengine re) {
    }

    public void   rLoadHistory  (Rengine re, String filename) {
    }

    public void   rSaveHistory  (Rengine re, String filename) {
    }
}


public class CNAController {
    JRIEngine jriEngine;
    org.rosuda.REngine.REXP rawInput, aves, traits, combat_edata, num;
    String directory;
    @FXML
    private ImageView card,card2;
    @FXML
    private ListView filesList1;
    @FXML
    private HBox root;

    @FXML
    private TextField numss, colorChooser;

    @FXML
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7, checkBox8, checkBox9, checkBox10, checkBox11, checkBox12, checkBox13, checkBox14, checkBox15, checkBox16;


    @FXML
    private void SelctFile(ActionEvent event) throws REngineException, REXPMismatchException {
        System.out.println(jriEngine.getRni());
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV file", "*.csv"));
        File file = fc.showOpenDialog(null);



        String fileName = file.getName();
        directory = file.getAbsolutePath().replace(fileName,"");
        System.out.println(fileName);
        System.out.println(directory);
//        jriEngine.assign("directory",directory);
//        jriEngine.assign("input.csv", fileName);
//        rawInput = jriEngine.parseAndEval(fileName);

        jriEngine.parseAndEval("setwd('/Users/kuldeep/Downloads/WGCNA')");
        rawInput = jriEngine.parseAndEval("read.csv('input.csv')");
        jriEngine.assign("rawData",rawInput);
        jriEngine.parseAndEval("datExpr <- as.data.frame((rawData[,-c(1:3)]))");
        jriEngine.parseAndEval("row.names(datExpr) <- rawData$combo");
        jriEngine.parseAndEval("row.names(datExpr)");
        jriEngine.parseAndEval("head(datExpr)");
        filesList1.getItems().add("Loaded inputs");
        checkBox2.setSelected(true);
    }

    @FXML
    private void LoadPackages(ActionEvent event) throws REngineException, REXPMismatchException {
        String[] args = new String[]{"--vanilla"};
        jriEngine = new JRIEngine(args, new TextConsole(), false);
        System.out.println("Rengine created, waiting for R");
        jriEngine.parseAndEval("library('Matrix')");
        jriEngine.parseAndEval("library('lme4')");
        jriEngine.parseAndEval("library('WGCNA')");
        jriEngine.parseAndEval("library('colorspace')");
        jriEngine.parseAndEval("library('pbkrtest')");
        jriEngine.parseAndEval("library('car')");
        jriEngine.parseAndEval("library('plot3D')");
        jriEngine.parseAndEval("library('sva')");
        jriEngine.parseAndEval("library('Biobase')");
        jriEngine.parseAndEval("library('flashClust')");
        jriEngine.parseAndEval("library('Rcpp')");
        jriEngine.parseAndEval("library('ggplot2')");
        jriEngine.parseAndEval("library('FactoMineR')");
        jriEngine.parseAndEval("library('pca3d')");
        jriEngine.parseAndEval("library('scatterplot3d')");
        jriEngine.parseAndEval("library('grid')");
        jriEngine.parseAndEval("library('splitstackshape')");
        jriEngine.parseAndEval("library('factoextra')");
        jriEngine.parseAndEval("library('gplots')");
        jriEngine.parseAndEval("library('RColorBrewer')");
        System.out.println(jriEngine.getRni());
        filesList1.getItems().add("Loaded packages");
        checkBox1.setSelected(true);

    }

    @FXML
    public void EliminateMFI(ActionEvent event) throws REXPMismatchException, REngineException {
        jriEngine.parseAndEval("aves <- rowMeans(datExpr)");
        jriEngine.parseAndEval("datExpr['new.col'] <- aves");
        aves = jriEngine.get("aves", null, false);
        System.out.println(aves.asDoubles().length);
        jriEngine.parseAndEval("datlog <- subset(datExpr, new.col>100)");
        jriEngine.parseAndEval("nrow(datlog) ");
        jriEngine.parseAndEval("head(datlog)");
        jriEngine.parseAndEval("ncol(datlog)->cols");
        jriEngine.parseAndEval("ncol(datlog)");
        jriEngine.parseAndEval("datlog <- datlog[,-cols]");
        jriEngine.parseAndEval("exprs <- as.matrix(datlog,header=TRUE, sep= '\t', row.names=5,as.is=TRUE)");
        jriEngine.parseAndEval("class(exprs)");
        jriEngine.parseAndEval("dim(exprs)");
        jriEngine.parseAndEval("colnames(exprs)");
        jriEngine.parseAndEval("traits <- read.csv('traits.csv', row.names =1)");
        traits = jriEngine.get("traits", null, false);
        System.out.println(traits.asList());
        jriEngine.parseAndEval("rownames(traits)   ");
        jriEngine.parseAndEval("all(rownames(traits)==colnames(exprs)) ");
        jriEngine.parseAndEval("colnames(exprs)");
        jriEngine.parseAndEval("names(traits)");
        filesList1.getItems().add("Eliminated MFI values less than 100");
        checkBox3.setSelected(true);
    }

    @FXML
    public void RunCombat(ActionEvent event) throws REXPMismatchException, REngineException, FileNotFoundException, InterruptedException {
        jriEngine.parseAndEval("metadata <- data.frame(labelDescription=c('experiment', 'treatment' ), row.names <- c('experiment', 'treatment'))");
        jriEngine.parseAndEval("phenoData=new('AnnotatedDataFrame',data=traits,varMetadata=metadata)");
        jriEngine.parseAndEval("names(traits)");
        jriEngine.parseAndEval("all(names(traits)==metadata$row.names)");
        jriEngine.parseAndEval("dataS <- ExpressionSet(assayData=exprs,phenoData=phenoData) ");
        jriEngine.parseAndEval("pheno <- pData(dataS)  ");
        jriEngine.parseAndEval("edata <- exprs(dataS)");
        jriEngine.parseAndEval("batch <- pheno$experiment");
        jriEngine.parseAndEval("modcombat <- model.matrix(~1, data=pheno)");
        jriEngine.parseAndEval("pdf('rplot.pdf') ");
        jriEngine.parseAndEval("combat_edata <- ComBat(dat=edata, batch=batch, mod=modcombat,par.prior=TRUE, prior.plots=TRUE)");
        jriEngine.parseAndEval("plot(combat_edata)");
        jriEngine.parseAndEval("dev.off()");
        combat_edata = jriEngine.get("combat_edata", null, false);
        System.out.println(combat_edata);
        jriEngine.parseAndEval("log2data <- log(combat_edata,2) ");
        jriEngine.parseAndEval("log2data2 <- log(edata,2)");
        jriEngine.parseAndEval("tdata <- t(log2data)");
        jriEngine.parseAndEval("tdata2 <- t(log2data2)");
        jriEngine.parseAndEval("png('postCombat.png') ");
        jriEngine.parseAndEval("plot(hclust(dist(tdata)), main='postCombat')");
        jriEngine.parseAndEval("dev.off()");
        Image postCombat = new Image(new FileInputStream( directory + "/postCombat.png"));
//        card.setImage(postCombat);
//        ImageView bottom = new ImageView(postCombat);

        jriEngine.parseAndEval("png('preCombat.png') ");
        jriEngine.parseAndEval("plot(hclust(dist(tdata2)),main='preCombat')");
        jriEngine.parseAndEval("dev.off()");
        jriEngine.parseAndEval("write.csv(combat_edata,'combated_data.csv')");
        Image preCombat  = new Image(new FileInputStream( directory + "/preCombat.png"));
//        ImageView top = new ImageView(preCombat);
//        ImageView view1 = getImageView(postCombat, 350, 300, false);
//        view1.setX(0.0);
//        view1.setY(0.0);
//        ImageView view2 = getImageView(preCombat, 350, 300, true);
//        view2.setX(0.0);
//        view2.setY(400.0);
//        root.getChildren().addAll(view1, view2);
//        root.setStyle("-fx-padding: 10;");
//        // Set the border-style of the HBox
//        root.setStyle("-fx-border-style: solid inside;");
//        // Set the border-width of the HBox
//        root.setStyle("-fx-border-width: 2;");
//        // Set the border-insets of the HBox
//        root.setStyle("-fx-border-insets: 5;");
//        // Set the border-radius of the HBox
//        root.setStyle("-fx-border-radius: 5;");
//        // Set the border-color of the HBox
//        root.setStyle("-fx-border-color: blue;");
//        // Set the size of the HBox
//        root.setPrefSize(800, 1000);
        card.setFitHeight(postCombat.getHeight());
        card.setFitWidth(postCombat.getWidth());
        card.setImage(postCombat);

        card2.setFitHeight(preCombat.getHeight());
        card2.setFitWidth(preCombat.getWidth());
        card2.setImage(preCombat);

//        card2.setImage(postCombat);
        filesList1.getItems().add("Combat step executed");
        checkBox4.setSelected(true);
    }

    private ImageView getImageView(Image image, double fitWidth,
                                   double fitHeight, boolean preserveRation)
    {
        // Create the ImageView
        ImageView view = new ImageView(image);
        // Set Properties Width, Height, Smooth and PreserveRatio
        view.setFitWidth(fitWidth);
        view.setFitHeight(fitHeight);
        view.setPreserveRatio(preserveRation);
        view.setSmooth(true);
        return view;
    }

    @FXML
    public void SetUpCombatedData(ActionEvent event) throws REXPMismatchException, REngineException, FileNotFoundException {

        jriEngine.parseAndEval("rawData <- read.csv('combated_data.csv')");
        jriEngine.parseAndEval("rawData2 <- as.data.frame(rawData)");
        jriEngine.parseAndEval("head(rawData2)");
        jriEngine.parseAndEval("datExpr <- as.data.frame(t(rawData2[,-c(1)]))");
        jriEngine.parseAndEval("datExpr <- log(datExpr,2)");
        jriEngine.parseAndEval("names(datExpr) <- rawData2$X");
        jriEngine.parseAndEval("head(datExpr)");
        jriEngine.parseAndEval("dim(datExpr)");
        jriEngine.parseAndEval("nGenes <- ncol(datExpr)");
        jriEngine.parseAndEval("nSamples <- nrow(datExpr)");
        jriEngine.parseAndEval("datTraits<-traits");
        jriEngine.parseAndEval("sampleTree2 <- hclust(dist(datExpr))");
        jriEngine.parseAndEval("traitColors <- numbers2colors(datTraits, signed = FALSE)");
        jriEngine.parseAndEval("jpeg('plotDendroAndColors.jpeg')");
        jriEngine.parseAndEval("plotDendroAndColors(sampleTree2, traitColors,groupLabels = names(datTraits),main = 'Sample dendrogram and trait heatmap')");
        jriEngine.parseAndEval("dev.off()");
        card.getImage().cancel();
        card2.setImage(null);
        System.gc();
        Image plotDendroAndColors = new Image(new FileInputStream( directory + "/plotDendroAndColors.jpeg"));
        card.setImage(plotDendroAndColors);

        filesList1.getItems().add("SetUp of Combated Data done");
        checkBox5.setSelected(true);
    }

    @FXML
    public void PCAOnPostCombatData(ActionEvent event) throws REXPMismatchException, REngineException, FileNotFoundException {

        jriEngine.parseAndEval("dev.off()");
        jriEngine.parseAndEval("png('PCA_Graph.png') ");
        jriEngine.parseAndEval("PCA(datExpr)->expPCA");

        jriEngine.parseAndEval("labels <- as.factor(c(1,1,1,1,2,2,2,2))");
        jriEngine.parseAndEval("fviz_pca_ind(expPCA, axes = c(1,2), addEllipses = TRUE, habillage = labels, ellipse.level=.6)");
        jriEngine.parseAndEval("dimdesc(expPCA, axes=c(1))->PCAdrivers1  ");
        jriEngine.parseAndEval("PCAdrivers1");
        filesList1.getItems().add("PCA On PostCombatData done");
        card.getImage().cancel();
        card2.setImage(null);
        Image plotDendroAndColors = new Image(new FileInputStream( directory + "/PCA_Graph.png"));
        card.setImage(plotDendroAndColors);
        checkBox6.setSelected(true);

    }

    @FXML
    public void RunCNA(ActionEvent event) throws REXPMismatchException, REngineException, FileNotFoundException {

        jriEngine.parseAndEval("dev.off()");
        jriEngine.parseAndEval("powers <- c(c(1:10), seq(from = 12, to=20, by=2))");
        jriEngine.parseAndEval("sft <- pickSoftThreshold(datExpr, powerVector = powers, verbose = 5)");
        //jriEngine.parseAndEval("sizeGrWindow(9, 5)");
        jriEngine.parseAndEval("png('HDBD1.png') ");
        jriEngine.parseAndEval("par(mfrow = c(1,2))");
        jriEngine.parseAndEval("cex1 <- 0.9");
        jriEngine.parseAndEval("plot(sft$fitIndices[,1],-sign(sft$fitIndices[,3])*sft$fitIndices[,2], xlab='Soft Threshold (power)', ylab='Scale Free Topology Model Fit,signed R^2',type='n',main = paste('Scale independence'))");
        jriEngine.parseAndEval("text(sft$fitIndices[,1],-sign(sft$fitIndices[,3])*sft$fitIndices[,2],labels=powers,cex=cex1,col='red')");


        jriEngine.parseAndEval("abline(h = 0.90,col = 'red')");
        jriEngine.parseAndEval("dev.off()");
        Image HDBD1 = new Image(new FileInputStream( directory + "/HDBD1.png"));
        jriEngine.parseAndEval("png('HDBD2.png') ");
        jriEngine.parseAndEval("plot(sft$fitIndices[,1], sft$fitIndices[,5],xlab='Soft Threshold (power)',ylab='Mean Connectivity',type='n',main = paste('Mean connectivity'))");
        jriEngine.parseAndEval("text(sft$fitIndices[,1], sft$fitIndices[,5],labels=powers, cex=cex1,col='red')");
        jriEngine.parseAndEval("dev.off()");
        Image HDBD2 = new Image(new FileInputStream( directory + "/HDBD2.png"));
        card.setImage(HDBD1);
        card2.setImage(HDBD2);
        checkBox7.setSelected(true);

    }

    @FXML
    public void ChooseASoftPower(ActionEvent event) throws REXPMismatchException, REngineException, FileNotFoundException {
        int power = Integer.parseInt(numss.getText());
        jriEngine.assign("power", new int[]{power});
        jriEngine.parseAndEval("softPower <- power");
        jriEngine.parseAndEval("minModuleSize= 10 ");
        jriEngine.parseAndEval("dev.off()");
        jriEngine.parseAndEval("adjacency <- adjacency(datExpr,power = softPower)");
        jriEngine.parseAndEval("TOM <- TOMsimilarity(adjacency)");
        jriEngine.parseAndEval("dissTOM <- 1-TOM");
        jriEngine.parseAndEval("geneTree <- flashClust(as.dist(dissTOM), method = 'average')");
        jriEngine.parseAndEval("png('geneTree.png')");
        jriEngine.parseAndEval("plot(geneTree)");
        jriEngine.parseAndEval("dev.off()");
        Image geneTree = new Image(new FileInputStream( directory + "/geneTree.png"));
        jriEngine.parseAndEval("dynamicMods <- cutreeDynamic(dendro = geneTree, distM = dissTOM, deepSplit = 2, pamRespectsDendro = FALSE, minClusterSize = minModuleSize)");
        jriEngine.parseAndEval("dynamicColors <- labels2colors(dynamicMods)");
        jriEngine.parseAndEval("MEList <- moduleEigengenes(datExpr, colors = dynamicColors)");
        jriEngine.parseAndEval("MEs <- MEList$eigengenes");
        jriEngine.parseAndEval("MEDiss <- 1-cor(MEs)");
        jriEngine.parseAndEval("METree <- flashClust(as.dist(MEDiss), method = 'average')");
        jriEngine.parseAndEval("MEDissThres <- 0.25");
        jriEngine.parseAndEval("merge <- mergeCloseModules(datExpr, dynamicColors, cutHeight = MEDissThres, verbose = 3)");
        jriEngine.parseAndEval("mergedColors <- merge$colors");
        jriEngine.parseAndEval("mergedMEs <- merge$newMEs");
        jriEngine.parseAndEval("PPInames <- colnames(datExpr)");
        //jriEngine.parseAndEval("sizeGrWindow(120,4)");
        jriEngine.parseAndEval("png('Cluster_DEndrogram.png')");
        jriEngine.parseAndEval("plotDendroAndColors(geneTree, cbind(dynamicColors, mergedColors), c(\"Dynamic Tree Cut\", \"Merged dynamic\"), cex.dendroLabels = 0.7, dendroLabels = PPInames, addGuide = TRUE, guideHang = 0.05)");
        jriEngine.parseAndEval("dev.off()");
        jriEngine.parseAndEval("moduleColors <- mergedColors");
        jriEngine.parseAndEval("colorOrder <- c('grey', standardColors(50))");
        jriEngine.parseAndEval("moduleLabels <- match(moduleColors, colorOrder)-1");
        jriEngine.parseAndEval("MEs <- mergedMEs");
        Image Cluster_DEndrogram = new Image(new FileInputStream( directory + "/Cluster_DEndrogram.png"));
        card.setImage(geneTree);
        card2.setImage(Cluster_DEndrogram);
        filesList1.getItems().add("Choose a soft power step done");
        checkBox8.setSelected(true);
    }

    @FXML
    public void CheckTOMPLOT(ActionEvent event) throws REXPMismatchException, REngineException, FileNotFoundException {
        jriEngine.parseAndEval("plotTOM <- dissTOM^3");
        jriEngine.parseAndEval("diag(plotTOM) <- NA");
        jriEngine.parseAndEval("png('tom.png')");
        jriEngine.parseAndEval("TOMplot(plotTOM, geneTree, moduleColors, main = 'Network heatmap plot, all genes')");
        jriEngine.parseAndEval("dev.off()");
        Image tom = new Image(new FileInputStream( directory + "/tom.png"));
        jriEngine.parseAndEval("png(file = paste0('PPI',softPower,minModuleSize,'.png'),wi = 500, he = 900)");
        jriEngine.parseAndEval("png('PPI.png')");
        jriEngine.parseAndEval("plotDendroAndColors(geneTree, cbind(dynamicColors, mergedColors), c(\"Dynamic Tree Cut\", \"Merged dynamic\"), dendroLabels = PPInames, addGuide = TRUE, guideHang = 0.05)");
        jriEngine.parseAndEval("dev.off()");

        Image PPI = new Image(new FileInputStream( directory + "/PPI.png"));
        card.setImage(tom);
        card2.setImage(PPI);
        filesList1.getItems().add("CheckTOMPLOT done");
        checkBox9.setSelected(true);
    }

    @FXML
    public void ModeulTraitRelationships(ActionEvent event) throws REXPMismatchException, REngineException, FileNotFoundException {

        jriEngine.parseAndEval("png('tom2.png')");
        jriEngine.parseAndEval("moduleTraitCor <- cor(MEs, datTraits, use = 'p')");
        jriEngine.parseAndEval("moduleTraitPvalue <- corPvalueStudent(moduleTraitCor, nSamples)");
        //jriEngine.parseAndEval("sizeGrWindow(10,6)");
        jriEngine.parseAndEval("textMatrix = paste(signif(moduleTraitCor, 2), '\n(' ,signif(moduleTraitPvalue, 1), ')', sep = '')");
        jriEngine.parseAndEval("dim(textMatrix) <- dim(moduleTraitCor)");

        jriEngine.parseAndEval("par(mar = c(6, 8.5, 3, 3))");
        jriEngine.parseAndEval("palette <- colorRampPalette(c('blue', 'white', 'white','red'))(n=299)");

        jriEngine.parseAndEval("labeledHeatmap(Matrix = moduleTraitCor,xLabels = names(datTraits),yLabels = names(MEs),ySymbols = names(MEs),colorLabels = FALSE ,colors = palette,textMatrix = textMatrix, setStdMargins = FALSE, cex.text = 0.5, zlim = c(-1,1),main = paste('Module-trait relationships'))");
        jriEngine.parseAndEval("dev.off()");
        Image tom2 = new Image(new FileInputStream( directory + "/tom2.png"));
        card2.setImage(null);
        card.setImage(tom2);
        filesList1.getItems().add("ModeulTraitRelationships done");
        checkBox10.setSelected(true);
    }

    @FXML
    public void DefineVariablesForOutputFileAndHeatmaps(ActionEvent event) throws REXPMismatchException, REngineException {
        jriEngine.parseAndEval("analTrait <- 'black' ");
        jriEngine.parseAndEval("analCondition <- 'treatment'  ");
        jriEngine.parseAndEval("weight <- as.data.frame(datTraits$treatment)  ");
        jriEngine.parseAndEval("names(weight) = 'treatment'");
        jriEngine.parseAndEval("aggregate(datExpr, list(datTraits$treatment), mean) -> means");
        jriEngine.parseAndEval("meanNames <- c('NMDA', 'ACSF')       ");
        filesList1.getItems().add("DefineVariablesForOutputFileAndHeatmaps done");
        checkBox11.setSelected(true);
    }

    @FXML
    public void NowRunIt(ActionEvent event) throws REXPMismatchException, REngineException {
        jriEngine.parseAndEval("module <- analTrait");
        jriEngine.parseAndEval("rownames(means) <- meanNames");
        jriEngine.parseAndEval("means <- means[,-c(1)]");
        jriEngine.parseAndEval("foldChange <- means[1,]/means[2,]");
        jriEngine.parseAndEval("means <- 2^means");
        jriEngine.parseAndEval("rownames(foldChange) <- 'FoldChange'");
        jriEngine.parseAndEval("modNames <- substring(names(MEs), 3)");
        jriEngine.parseAndEval("geneModuleMembership <- as.data.frame(cor(datExpr, MEs, use = 'p'))");
        jriEngine.parseAndEval("MMPvalue <- as.data.frame(corPvalueStudent(as.matrix(geneModuleMembership), nSamples))");
        jriEngine.parseAndEval("names(geneModuleMembership) <- paste('MM', modNames, sep='')");
        jriEngine.parseAndEval("names(MMPvalue) = paste('p.MM', modNames, sep='')");
        jriEngine.parseAndEval("geneTraitSignificance <- as.data.frame(cor(datExpr, weight, use ='p'))");
        jriEngine.parseAndEval("GSPvalue <- as.data.frame(corPvalueStudent(as.matrix(geneTraitSignificance), nSamples))");
        jriEngine.parseAndEval("names(geneTraitSignificance) = paste('GS', names(weight), sep='')");
        jriEngine.parseAndEval("names(GSPvalue) = paste('p.GS.', names(weight), sep='')");
        jriEngine.parseAndEval("column <- match(module, modNames)");
        jriEngine.parseAndEval("moduleGenes <- moduleColors==module");
        jriEngine.parseAndEval("probes <- colnames(datExpr)");
        jriEngine.parseAndEval("geneInfo0 <- data.frame(probes = probes, moduleColor = moduleColors, t(means), geneTraitSignificance, GSPvalue)");
        jriEngine.parseAndEval("modOrder <- order(-abs(cor(MEs, weight, use = 'p')))");
        jriEngine.parseAndEval(" num <- ncol(geneModuleMembership)");
        jriEngine.parseAndEval("for (mod in 1:ncol(geneModuleMembership)) { oldNames <- names(geneInfo0);  geneInfo0 <- data.frame(geneInfo0, geneModuleMembership[, modOrder[mod]],MMPvalue[, modOrder[mod]]);  names(geneInfo0) <- c(oldNames, paste('MM.', modNames[modOrder[mod]], sep=''),paste('p.MM.', modNames[modOrder[mod]], sep=''))}");
        jriEngine.parseAndEval("moi1 <- 'p.MM'");
        jriEngine.parseAndEval("geneorderspacer <- paste(moi1,analTrait, sep='.')");
        jriEngine.parseAndEval("geneOrder <- order(abs(geneInfo0[,geneorderspacer])) ");
        jriEngine.parseAndEval("geneInfo <- geneInfo0[geneOrder, ]");
        filesList1.getItems().add("NowRunIt done");
        checkBox12.setSelected(true);
    }

    @FXML
    public void NameTheCNA(ActionEvent event) throws REXPMismatchException, REngineException {
        jriEngine.parseAndEval("write.csv(geneInfo, file = 'CNA_outputs.csv')");
        filesList1.getItems().add("NameTheCNA done");
        checkBox13.setSelected(true);
    }

    @FXML
    public void MakeModuleHeatMaps(ActionEvent event) throws REXPMismatchException, REngineException {
        String clor = colorChooser.getText();
        jriEngine.assign("color", clor);
        jriEngine.parseAndEval("MOI <- color");
        filesList1.getItems().add("MakeModuleHeatMaps done");
        checkBox14.setSelected(true);
    }

    @FXML
    public void RunModuleHeatMaps(ActionEvent event) throws REXPMismatchException, REngineException, FileNotFoundException {
        jriEngine.parseAndEval("my_palette <- colorRampPalette(c('blue','white','red'))(n = 299)");
        jriEngine.parseAndEval("colors = c(seq(-1,-.11, length=145),eq(-.1,.1,length=10), seq(0.11,1,length=145))");
        jriEngine.parseAndEval("datHeatmap <- data.frame(t(datExpr),MMPvalue,t(foldChange),geneTraitSignificance)");
        jriEngine.parseAndEval("moiname1 <- paste(moi1,MOI, sep='.')");
        jriEngine.parseAndEval("moiname2 <- paste(moi1,MOI, sep='')");
        jriEngine.parseAndEval("mapOrder <- order(abs(geneInfo0[,moiname1]))   ");
        jriEngine.parseAndEval("datHeatM <- datHeatmap[mapOrder, ]");
        jriEngine.parseAndEval("sortedHeat <- subset(datHeatM,datHeatM[,moiname2]<.05)");
        jriEngine.parseAndEval("as.matrix(sortedHeat[,c(1:8)])->aa        ");
        jriEngine.parseAndEval("hclust_fun <-  function(x) hclust(x, method = 'ward')");
        jriEngine.parseAndEval("png('output.png')");
        jriEngine.parseAndEval("heatmap.2 (aa, col= my_palette, dendrogram = 'both', scale = 'row', key = T, keysize = 1,density.info = 'none',trace = 'none',hclustfun = hclust_fun,cexCol = .8, cexRow = .5, Colv = TRUE, Rowv = TRUE,margin = c(5,15),srtCol = 45,sepcolor = 'black', sepwidth= c(.01, .01),key.xlab = 'Relative Scaled Value',key.title = NA, main = MOI)");
        jriEngine.parseAndEval("dev.off()");
        Image output = new Image(new FileInputStream( directory + "/output.png"));
        card2.setImage(null);
        card.setImage(output);
        filesList1.getItems().add("RunModuleHeatMaps done");
        checkBox15.setSelected(true);
    }

    @FXML
    public void CreateANCandCNAHeatMap(ActionEvent event) throws REXPMismatchException, REngineException, FileNotFoundException {
        jriEngine.parseAndEval("write.csv(aa, 'aa.csv')       ");
        jriEngine.parseAndEval("read.csv('aa.csv', row.names = 1)->bb");
        jriEngine.parseAndEval("as.matrix(bb)->bb");
        jriEngine.parseAndEval("png('ANCCNA.png')");
        jriEngine.parseAndEval("heatmap.2 (bb, ol= my_palette,dendrogram = 'col', scale = 'row', key = T,  keysize = 1, density.info = 'none',trace = 'none',hclustfun = hclust_fun, cexCol = .8, cexRow = .7, Colv = TRUE, Rowv = FALSE, margin = c(5,15), srtCol = 45, sepcolor = 'black',sepwidth= c(.01, .01), key.xlab = 'Relative Scaled Value', key.title = NA, main = 'ANCnCNA Hits')");
        jriEngine.parseAndEval("dev.off()");
        Image ANCCNA = new Image(new FileInputStream( directory + "/ANCCNA.png"));
        card2.setImage(null);
        card.setImage(ANCCNA);
        filesList1.getItems().add("CreateANCandCNAHeatMap done");
        checkBox16.setSelected(true);
    }
}
