// MongoDB script to populate article_content and interactions collections

// Function to generate random article content
function generateArticleContent(title, category) {
  const paragraphs = [];
  const paragraphCount = Math.floor(Math.random() * 5) + 3; // 3-7 paragraphs
  
  // Introduction paragraph
  paragraphs.push(`# ${title}\n\nWelcome to this article about ${title.toLowerCase()}. This piece explores ${category.toLowerCase()} concepts and provides valuable insights into this fascinating topic.`);
  
  // Body paragraphs with category-specific content
  for (let i = 0; i < paragraphCount; i++) {
    let paragraph = '';
    
    if (category === 'Technology') {
      const techTopics = [
        "The evolution of technology continues to accelerate, transforming how we live, work, and interact with the world around us. Innovations in this space are driving unprecedented change across industries.",
        "Software development practices have evolved significantly in recent years, with new methodologies and tools emerging to address the complex challenges faced by today's developers.",
        "Cloud computing has revolutionized infrastructure management, allowing for scalable, flexible, and cost-effective solutions that power modern digital experiences.",
        "Cybersecurity remains a critical concern as our digital footprint expands. Organizations must implement robust security measures to protect sensitive data and systems.",
        "The Internet of Things (IoT) connects everyday objects to the internet, creating smart environments that can respond to our needs and preferences in real-time."
      ];
      paragraph = techTopics[Math.floor(Math.random() * techTopics.length)];
    } 
    else if (category === 'Science') {
      const scienceTopics = [
        "Scientific research continues to push the boundaries of our understanding, revealing new insights into the fundamental nature of reality and our place within it.",
        "The scientific method provides a structured approach to inquiry, allowing researchers to test hypotheses and build upon established knowledge in a systematic way.",
        "Collaboration between researchers across disciplines is essential for addressing complex scientific challenges, as diverse perspectives often lead to breakthrough discoveries.",
        "Ethical considerations in scientific research are paramount, ensuring that advances in knowledge benefit humanity while minimizing potential harms.",
        "Public understanding of science is critical for informed decision-making on issues ranging from climate change to public health policies."
      ];
      paragraph = scienceTopics[Math.floor(Math.random() * scienceTopics.length)];
    }
    else if (category === 'Business') {
      const businessTopics = [
        "Effective leadership is essential for navigating today's complex business landscape, requiring a combination of strategic vision, emotional intelligence, and adaptability.",
        "Market trends indicate a shift towards more sustainable and socially responsible business practices, driven by changing consumer preferences and regulatory pressures.",
        "Digital transformation has become a strategic imperative for organizations seeking to remain competitive in an increasingly technology-driven marketplace.",
        "Entrepreneurship drives economic growth by introducing innovative products, services, and business models that create value for customers and society.",
        "Organizational culture significantly impacts business performance, influencing everything from employee engagement and retention to innovation and customer satisfaction."
      ];
      paragraph = businessTopics[Math.floor(Math.random() * businessTopics.length)];
    }
    else if (category === 'Health') {
      const healthTopics = [
        "Preventive healthcare approaches focus on maintaining wellness rather than treating disease, encompassing regular screenings, healthy lifestyle choices, and risk management strategies.",
        "Nutritional science continues to evolve, providing new insights into how dietary choices affect overall health, disease risk, and longevity.",
        "Mental health awareness has grown significantly in recent years, reducing stigma and increasing access to support resources and treatment options.",
        "Exercise physiology research demonstrates the numerous benefits of physical activity for both physical and mental wellbeing across the lifespan.",
        "Healthcare technology innovations are transforming patient care, from telemedicine platforms that increase access to care to AI-powered diagnostic tools that improve accuracy."
      ];
      paragraph = healthTopics[Math.floor(Math.random() * healthTopics.length)];
    }
    else if (category === 'Culture') {
      const cultureTopics = [
        "Cultural heritage preservation efforts safeguard tangible and intangible traditions for future generations, maintaining connections to our collective past while acknowledging their evolving nature.",
        "The arts play a vital role in society, serving as both a mirror reflecting contemporary issues and a window into alternative perspectives and possibilities.",
        "Cross-cultural communication skills have become increasingly important in our globalized world, facilitating understanding and collaboration across diverse contexts.",
        "Popular culture both shapes and reflects societal values, providing a common language through which people process and discuss shared experiences.",
        "Digital media has transformed how cultural content is created, distributed, and consumed, democratizing access while presenting new challenges for creators."
      ];
      paragraph = cultureTopics[Math.floor(Math.random() * cultureTopics.length)];
    }
    
    paragraphs.push(paragraph);
  }
  
  // Conclusion paragraph
  paragraphs.push("## Conclusion\n\nIn conclusion, this article has explored various aspects of " + title.toLowerCase() + ". We hope you found this information valuable and that it provides a foundation for further exploration of this fascinating topic.");
  
  // Join all paragraphs with double newlines
  return paragraphs.join("\n\n");
}

// Connect to MongoDB
db = db.getSiblingDB('greenhub');

// Clear existing data
db.article_content.drop();
db.interactions.drop();

// Insert article content for all 20 articles from PostgreSQL
// This script assumes the articles with IDs 1-22 exist in PostgreSQL

let articleCategories = {
  1: "Technology", 2: "Technology", 3: "Technology", 4: "Technology", 5: "Science", 
  6: "Science", 7: "Science", 8: "Science", 9: "Business", 10: "Business", 
  11: "Business", 12: "Business", 13: "Health", 14: "Health", 15: "Health", 
  16: "Health", 17: "Culture", 18: "Culture", 19: "Culture", 20: "Culture",
  21: "Technology", 22: "Science"
};

let articleTitles = {
  1: "The Future of Quantum Computing", 
  2: "AI in Healthcare: Transforming Patient Care", 
  3: "Blockchain Beyond Cryptocurrency", 
  4: "The Rise of Edge Computing", 
  5: "Breakthrough in Nuclear Fusion Research", 
  6: "Dark Matter: The Hunt Continues", 
  7: "CRISPR Gene Editing: Ethical Considerations", 
  8: "Climate Change: New Models and Predictions", 
  9: "Remote Work Revolution: The New Normal", 
  10: "Sustainable Business Practices That Boost Profit", 
  11: "The Gig Economy in 2025", 
  12: "Venture Capital Trends in Emerging Markets", 
  13: "Nutrition Myths Debunked", 
  14: "Mental Health in the Digital Age", 
  15: "Advances in Cancer Immunotherapy", 
  16: "Sleep Science: New Research and Insights", 
  17: "The Evolution of Virtual Reality in Art", 
  18: "Global Fusion Cuisine: Cultural Exchange Through Food", 
  19: "The Resurgence of Independent Cinema", 
  20: "Traditional Crafts in the Modern Marketplace",
  21: "Emerging Programming Languages to Watch",
  22: "Renewable Energy Breakthroughs"
};

// Add article content for each article
for (let articleId = 1; articleId <= 22; articleId++) {
  let content = generateArticleContent(articleTitles[articleId], articleCategories[articleId]);
  
  db.article_content.insertOne({
    articleId: articleId,
    content: content
  });
}

// Add interactions
// We have 10 regular users (user_id 4-13) and 3 admin users (user_id 1-3)
// Each user will have 5 random interactions

// Helper function to generate random interactions
function generateRandomInteractions(userId) {
  const interactions = [];
  
  // Select 5 random articles for this user to interact with
  const articleCount = 22;
  const articleIds = [];
  
  while (articleIds.length < 5) {
    const randomArticleId = Math.floor(Math.random() * articleCount) + 1;
    if (!articleIds.includes(randomArticleId)) {
      articleIds.push(randomArticleId);
    }
  }
  
  // Generate interactions for each selected article
  for (const articleId of articleIds) {
    // Randomly decide if the user liked the article (50% chance)
    const liked = Math.random() > 0.5;
    
    // Generate random number of views (1-5)
    const views = Math.floor(Math.random() * 5) + 1;
    
    // Generate random rating (1-5)
    const rating = Math.floor(Math.random() * 5) + 1;
    
    interactions.push({
      userId: userId,
      articleId: articleId,
      like: liked,
      views: views,
      rating: rating
    });
  }
  
  return interactions;
}

// Generate and insert interactions for all users (including admins)
for (let userId = 1; userId <= 13; userId++) {
  const userInteractions = generateRandomInteractions(userId);
  
  db.interactions.insertMany(userInteractions);
}

// Create indexes for better query performance
db.article_content.createIndex({ "articleId": 1 }, { unique: true });
db.interactions.createIndex({ "userId": 1, "articleId": 1 }, { unique: true });

// Output confirmation
print("MongoDB population complete!");
print(`Added content for ${db.article_content.count()} articles`);
print(`Added ${db.interactions.count()} user interactions`);