// MongoDB script to populate article_content and interactions collections

// Function to generate random article content
function generateArticleContent(title, category) {
  const paragraphs = [];
  const paragraphCount = Math.floor(Math.random() * 5) + 3; // 3-7 paragraphs
  
  // Introduction paragraph
  paragraphs.push(`# ${title}\n\nДобро пожаловать в статью о ${title.toLowerCase()}. Эта статья рассматривает вопросы, связанные с ${category.toLowerCase()}, и предоставляет ценные идеи по данной теме.`);
  
  // Body paragraphs with category-specific content
  for (let i = 0; i < paragraphCount; i++) {
    let paragraph = '';
    
    if (category === 'Климат') {
      const climateTopics = [
        "Изменение климата представляет одну из главных угроз для нашей планеты, требуя немедленных действий для сокращения выбросов парниковых газов и адаптации к новым условиям.",
        "Международные соглашения, такие как Парижское соглашение, играют ключевую роль в координации усилий по борьбе с глобальным потеплением.",
        "Климатические модели помогают ученым прогнозировать будущие изменения и разрабатывать стратегии для смягчения последствий.",
        "Рост экстремальных погодных явлений, таких как ураганы и засухи, подчеркивает необходимость устойчивых решений для защиты сообществ.",
        "Образование и информирование общественности о климатических проблемах способствуют формированию экологически ответственного поведения."
      ];
      paragraph = climateTopics[Math.floor(Math.random() * climateTopics.length)];
    } 
    else if (category === 'Биоразнообразие') {
      const biodiversityTopics = [
        "Сохранение биоразнообразия критически важно для поддержания экосистем, которые обеспечивают человечество пищей, водой и чистым воздухом.",
        "Защита природных заповедников помогает сохранять уникальные виды животных и растений, находящихся под угрозой исчезновения.",
        "Изменение климата и антропогенная деятельность угрожают биоразнообразию, требуя комплексных мер для восстановления экосистем.",
        "Экологическое образование играет важную роль в повышении осведомленности о значении биоразнообразия.",
        "Международное сотрудничество необходимо для защиты мигрирующих видов и сохранения их среды обитания."
      ];
      paragraph = biodiversityTopics[Math.floor(Math.random() * biodiversityTopics.length)];
    }
    else if (category === 'Энергия') {
      const energyTopics = [
        "Возобновляемые источники энергии, такие как солнечная и ветровая, становятся все более доступными и эффективными, снижая зависимость от ископаемого топлива.",
        "Энергоэффективные технологии помогают сократить потребление энергии в домах, офисах и на производстве.",
        "Инновации в области хранения энергии, такие как аккумуляторы нового поколения, делают возобновляемую энергию более надежной.",
        "Переход к чистой энергетике требует значительных инвестиций в инфраструктуру и государственную поддержку.",
        "Локальные энергетические проекты, такие как микросети, способствуют устойчивому развитию сельских регионов."
      ];
      paragraph = energyTopics[Math.floor(Math.random() * energyTopics.length)];
    }
    else if (category === 'Устойчивое развитие') {
      const sustainabilityTopics = [
        "Устойчивое развитие направлено на удовлетворение текущих потребностей без ущерба для будущих поколений.",
        "Экономика замкнутого цикла минимизирует отходы и способствует повторному использованию ресурсов.",
        "Зеленая урбанизация помогает создавать города, которые минимизируют экологический след и улучшают качество жизни.",
        "Образование и повышение осведомленности о принципах устойчивого развития необходимы для изменения поведения общества.",
        "Корпоративная ответственность включает внедрение экологичных практик в бизнес-процессы."
      ];
      paragraph = sustainabilityTopics[Math.floor(Math.random() * sustainabilityTopics.length)];
    }
    else if (category === 'Экологические технологии') {
      const ecoTechTopics = [
        "Инновационные технологии переработки помогают сократить количество отходов и вернуть материалы в производственный цикл.",
        "Искусственный интеллект используется для мониторинга окружающей среды, предоставляя данные для принятия экологических решений.",
        "Экологичные строительные материалы, такие как переработанный пластик, снижают воздействие строительства на окружающую среду.",
        "Умные системы управления отходами оптимизируют процессы сбора, сортировки и переработки мусора.",
        "Технологии очистки воды делают водоемы более безопасными для экосистем и человека."
      ];
      paragraph = ecoTechTopics[Math.floor(Math.random() * ecoTechTopics.length)];
    }
    
    paragraphs.push(paragraph);
  }
  
  // Conclusion paragraph
  paragraphs.push("## Заключение\n\nВ заключение, данная статья рассмотрела различные аспекты темы " + title.toLowerCase() + ". Мы надеемся, что предоставленная информация окажется полезной и станет основой для дальнейшего изучения этой важной темы.");
  
  // Join all paragraphs with double newlines
  return paragraphs.join("\n\n");
}

// Connect to MongoDB
db = db.getSiblingDB('greenhub');

// Clear existing data
db.article_content.drop();
db.interactions.drop();

// Insert article content for all 22 articles from PostgreSQL
let articleCategories = {
  1: "Климат", 2: "Климат", 3: "Климат", 4: "Климат", 
  5: "Биоразнообразие", 6: "Биоразнообразие", 7: "Биоразнообразие", 8: "Биоразнообразие", 
  9: "Энергия", 10: "Энергия", 11: "Энергия", 12: "Энергия", 
  13: "Устойчивое развитие", 14: "Устойчивое развитие", 15: "Устойчивое развитие", 16: "Устойчивое развитие", 
  17: "Экологические технологии", 18: "Экологические технологии", 19: "Экологические технологии", 20: "Экологические технологии",
  21: "Экологические технологии", 22: "Энергия"
};

let articleTitles = {
  1: "Будущее климатической политики в России", 
  2: "Последствия таяния ледников в Арктике", 
  3: "Климатические беженцы: вызовы XXI века", 
  4: "Углеродный след: как его сократить", 
  5: "Сохранение редких видов в Сибири", 
  6: "Роль пчел в экосистемах", 
  7: "Морские заповедники: защита океанов", 
  8: "Инвазивные виды: угроза экосистемам", 
  9: "Солнечная энергия в России: перспективы", 
  10: "Ветроэнергетика: новые технологии", 
  11: "Энергоэффективность зданий", 
  12: "Гидроэнергия: плюсы и минусы", 
  13: "Экологичный образ жизни: с чего начать", 
  14: "Экономика замкнутого цикла", 
  15: "Зеленые города: будущее урбанизации", 
  16: "Корпоративная социальная ответственность", 
  17: "Технологии переработки пластика", 
  18: "Умные системы управления отходами", 
  19: "Экологичные строительные материалы", 
  20: "ИИ для мониторинга окружающей среды",
  21: "Инновации в очистке водоемов",
  22: "Возобновляемая энергия в сельских регионах"
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
// We have 10 regular users (user_id 1-10) since we don't have admin users specified
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

// Generate and insert interactions for all users
for (let userId = 1; userId <= 10; userId++) {
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